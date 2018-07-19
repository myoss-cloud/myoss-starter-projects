/*
 * Copyright 2018-2018 https://github.com/myoss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.myoss.phoenix.core.lang.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 多线程执行框架
 *
 * @author Jerry.Chen
 * @since 2018年6月4日 下午11:17:29
 */
@Slf4j
public class ExecutorEngine implements AutoCloseable {
    private static final ThreadPoolExecutor SHUTDOWN_EXECUTOR = new ThreadPoolExecutor(0, 1, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10),
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Phoenix-Core-ExecutorEngineCloseTimer").build());

    @Getter
    private final ExecutorService           executorService;

    /**
     * 创建多线程执行框架
     *
     * @param delegate an instance of {@link ExecutorService}
     */
    public ExecutorEngine(ExecutorService delegate) {
        this.executorService = MoreExecutors.listeningDecorator(delegate);
        // 添加一个关闭的钩子来等待 executorService 中的线程完成
        MoreExecutors.addDelayedShutdownHook(this.executorService, 60, TimeUnit.SECONDS);
    }

    /**
     * 创建 ThreadPoolExecutor 多线程池。默认值如下：
     *
     * <pre>
     *  corePoolSize 指的是保留的线程池大小，默认值为：5。
     *  maximumPoolSize 指的是线程池的最大大小，默认值为：200。
     *  keepAliveTime 指的是空闲线程结束的超时时间，默认值为：0。
     *  unit 是一个枚举，表示 keepAliveTime 的单位，默认值为：{@link TimeUnit#MILLISECONDS}。
     *  workQueue 表示存放任务的队列，默认值为：1024。
     * </pre>
     *
     * @return ThreadPoolExecutor 多线程池执行框架
     */
    public static ExecutorEngine buildTreadPoolExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true)
                .setNameFormat("ExecutorEngineThreadPool-%d")
                .build();
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 200, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), threadFactory);
        return new ExecutorEngine(poolExecutor);
    }

    /**
     * 创建可以追踪调用链的多线程池
     *
     * @param beanFactory Spring BeanFactory
     * @param delegate 代理的线程池
     * @return 可以追踪调用链的多线程池执行框架
     */
    public static ExecutorEngine buildTraceableExecutorService(BeanFactory beanFactory, ExecutorService delegate) {
        TraceableExecutorService traceableExecutorService = new TraceableExecutorService(beanFactory, delegate);
        return new ExecutorEngine(traceableExecutorService);
    }

    @Override
    public void close() {
        SHUTDOWN_EXECUTOR.execute(() -> {
            try {
                executorService.shutdown();
                while (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (final InterruptedException ex) {
                log.error("ExecutorEngine can not been terminated", ex);
            }
        });
    }

    /**
     * 多线程执行任务.
     *
     * @param inputs 输入参数
     * @param executeUnit 执行单元
     * @param <I> 入参类型
     * @param <O> 出参类型
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public <I, O> List<O> execute(final Collection<I> inputs, final ExecuteUnit<I, O> executeUnit) {
        if (inputs.size() == 1) {
            try {
                return Lists.newArrayList(executeUnit.execute(inputs.iterator().next()));
            } catch (Exception ex) {
                throw new ExecuteException("execute task throw exception", ex);
            }
        }
        ListenableFuture<List<O>> futures = submitFutures(inputs, executeUnit);
        addCallback(futures);
        return getFutureResults(futures);
    }

    /**
     * 多线程执行任务.
     *
     * @param size 最多执行几次
     * @param executeUnit 执行单元
     * @param <O> 出参类型
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public <O> List<O> execute(final int size, final ExecuteUnit<Integer, O> executeUnit) {
        if (size == 1) {
            try {
                return Lists.newArrayList(executeUnit.execute(0));
            } catch (Exception ex) {
                throw new ExecuteException("execute task throw exception", ex);
            }
        }
        List<Integer> inputs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            inputs.add(i);
        }
        ListenableFuture<List<O>> futures = submitFutures(inputs, executeUnit);
        addCallback(futures);
        return getFutureResults(futures);
    }

    /**
     * 多线程执行任务并归并结果.
     *
     * @param inputs 执行入参
     * @param executeUnit 执行单元
     * @param mergeUnit 合并结果单元
     * @param <I> 入参类型
     * @param <M> 中间结果类型
     * @param <O> 最终结果类型
     * @return 执行结果
     */
    public <I, M, O> O execute(final Collection<I> inputs, final ExecuteUnit<I, M> executeUnit,
                               final MergeUnit<M, O> mergeUnit) {
        return mergeUnit.merge(execute(inputs, executeUnit));
    }

    /**
     * 提交多线程任务.
     *
     * @param inputs 执行入参
     * @param executeUnit 执行单元
     * @param <I> 入参类型
     * @param <O> 最终结果类型
     * @return 执行结果
     */
    private <I, O> ListenableFuture<List<O>> submitFutures(final Collection<I> inputs,
                                                           final ExecuteUnit<I, O> executeUnit) {
        Set<ListenableFuture<O>> result = new HashSet<>(inputs.size());
        for (final I each : inputs) {
            result.add((ListenableFuture<O>) executorService.submit(() -> executeUnit.execute(each)));
        }
        return Futures.allAsList(result);
    }

    /**
     * 为多线程任务添加回调监控
     *
     * @param allFutures 多线程任务
     * @param <O> 最终结果类型
     */
    private <O> void addCallback(final ListenableFuture<O> allFutures) {
        Futures.addCallback(allFutures, new FutureCallback<O>() {
            @Override
            public void onSuccess(O result) {
                log.trace("Concurrent execute result success {}", result);
            }

            @Override
            public void onFailure(Throwable thrown) {
                log.warn("Concurrent execute result failure", thrown);
            }
        }, executorService);
    }

    /**
     * 获取多线程任务执行的最终结果
     *
     * @param allFutures 多线程任务
     * @param <O> 最终结果类型
     * @return 执行结果
     */
    private <O> O getFutureResults(final ListenableFuture<O> allFutures) {
        try {
            return allFutures.get();
        } catch (final InterruptedException | ExecutionException ex) {
            // 其它异常信息，使用自定义异常进行包装
            throw new ExecuteException("execute task throw exception", ex);
        }
    }
}
