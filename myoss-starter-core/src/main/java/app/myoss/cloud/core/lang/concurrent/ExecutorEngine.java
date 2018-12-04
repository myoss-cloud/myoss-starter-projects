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

package app.myoss.cloud.core.lang.concurrent;

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
import java.util.concurrent.TimeoutException;

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
            new ThreadFactoryBuilder().setDaemon(true)
                    .setNameFormat("MyOSSCloud-Core-ExecutorEngineCloseTimer")
                    .build());

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
     * 多线程执行任务. 多个执行单元，使用相同的输入参数，进行多线程请求
     *
     * <pre>
     * // Demo示例
     * String param = &quot;&#39118;&#38738;&#26472;\uD83D\uDE0D&quot;;
     * List&lt;ExecuteUnit&lt;String, String&gt;&gt; executeUnits = new ArrayList&lt;&gt;();
     * executeUnits.add(EmojiUtils::addBackslash);
     * executeUnits.add(EmojiUtils::removeBackslash);
     * List&lt;String&gt; execute = executorEngine.execute(param, executeUnits, timeout, timeUnit);
     * </pre>
     *
     * @param input 输入参数
     * @param executeUnits 多个执行单元
     * @param timeout 执行超时时间（可选参数），因为是并发去执行，线程足够多的时候，全部执行下来，只需要花费单个执行的时间（无限接近）
     * @param timeUnit 执行超时时间单位（可选参数，如果设置了 timeout，没有设置 timeUnit，则默认使用
     *            {@link TimeUnit#MILLISECONDS}）
     * @param <I> 入参类型
     * @param <O> 出参类型
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public <I, O> List<O> execute(final I input, final Collection<ExecuteUnit<I, O>> executeUnits, Long timeout,
                                  TimeUnit timeUnit) {
        if (executeUnits.size() == 1) {
            try {
                return Lists.newArrayList(executeUnits.iterator().next().execute(input));
            } catch (Exception ex) {
                throw new ExecuteException("execute task throw exception", ex);
            }
        }
        ListenableFuture<List<O>> futures = submitFutures(input, executeUnits);
        addCallback(futures);
        return getFutureResults(futures, timeout, timeUnit);
    }

    /**
     * 多线程执行任务.
     *
     * @param inputs 输入参数
     * @param executeUnit 执行单元
     * @param timeout 执行超时时间（可选参数），因为是并发去执行，线程足够多的时候，全部执行下来，只需要花费单个执行的时间（无限接近）
     * @param timeUnit 执行超时时间单位（可选参数，如果设置了 timeout，没有设置 timeUnit，则默认使用
     *            {@link TimeUnit#MILLISECONDS}）
     * @param <I> 入参类型
     * @param <O> 出参类型
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public <I, O> List<O> execute(final Collection<I> inputs, final ExecuteUnit<I, O> executeUnit, Long timeout,
                                  TimeUnit timeUnit) {
        if (inputs.size() == 1) {
            try {
                return Lists.newArrayList(executeUnit.execute(inputs.iterator().next()));
            } catch (Exception ex) {
                throw new ExecuteException("execute task throw exception", ex);
            }
        }
        ListenableFuture<List<O>> futures = submitFutures(inputs, executeUnit);
        addCallback(futures);
        return getFutureResults(futures, timeout, timeUnit);
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
        return execute(inputs, executeUnit, null, null);
    }

    /**
     * 多线程执行任务.
     *
     * @param size 最多执行几次
     * @param executeUnit 执行单元
     * @param timeout 执行超时时间（可选参数），因为是并发去执行，线程足够多的时候，全部执行下来，只需要花费单个执行的时间（无限接近）
     * @param timeUnit 执行超时时间单位（可选参数，如果设置了 timeout，没有设置 timeUnit，则默认使用
     *            {@link TimeUnit#MILLISECONDS}）
     * @param <O> 出参类型
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public <O> List<O> execute(final int size, final ExecuteUnit<Integer, O> executeUnit, Long timeout,
                               TimeUnit timeUnit) {
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
        return getFutureResults(futures, timeout, timeUnit);
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
        return execute(size, executeUnit, null, null);
    }

    /**
     * 多线程执行任务并归并结果.
     *
     * @param inputs 执行入参
     * @param executeUnit 执行单元
     * @param mergeUnit 合并结果单元
     * @param timeout 执行超时时间（可选参数），因为是并发去执行，线程足够多的时候，全部执行下来，只需要花费单个执行的时间（无限接近）
     * @param timeUnit 执行超时时间单位（可选参数，如果设置了 timeout，没有设置 timeUnit，则默认使用
     *            {@link TimeUnit#MILLISECONDS}）
     * @param <I> 入参类型
     * @param <M> 中间结果类型
     * @param <O> 最终结果类型
     * @return 执行结果
     */
    public <I, M, O> O execute(final Collection<I> inputs, final ExecuteUnit<I, M> executeUnit,
                               final MergeUnit<M, O> mergeUnit, Long timeout, TimeUnit timeUnit) {
        return mergeUnit.merge(execute(inputs, executeUnit, timeout, timeUnit));
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
    public <I, O> ListenableFuture<List<O>> submitFutures(final Collection<I> inputs,
                                                          final ExecuteUnit<I, O> executeUnit) {
        Set<ListenableFuture<O>> result = new HashSet<>(inputs.size());
        for (final I each : inputs) {
            result.add((ListenableFuture<O>) executorService.submit(() -> executeUnit.execute(each)));
        }
        return Futures.allAsList(result);
    }

    /**
     * 提交多线程任务.
     *
     * @param input 执行入参
     * @param executeUnits 多个执行单元
     * @param <I> 入参类型
     * @param <O> 最终结果类型
     * @return 执行结果
     */
    public <I, O> ListenableFuture<List<O>> submitFutures(final I input,
                                                          final Collection<ExecuteUnit<I, O>> executeUnits) {
        Set<ListenableFuture<O>> result = new HashSet<>(executeUnits.size());
        for (ExecuteUnit<I, O> each : executeUnits) {
            result.add((ListenableFuture<O>) executorService.submit(() -> each.execute(input)));
        }
        return Futures.allAsList(result);
    }

    /**
     * 为多线程任务添加回调监控
     *
     * @param allFutures 多线程任务
     * @param <O> 最终结果类型
     */
    public <O> void addCallback(final ListenableFuture<O> allFutures) {
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
     * @param timeout 执行超时时间（可选参数），因为是并发去执行，线程足够多的时候，全部执行下来，只需要花费单个执行的时间（无限接近）
     * @param timeUnit 执行超时时间单位（可选参数，如果设置了 timeout，没有设置 timeUnit，则默认使用
     *            {@link TimeUnit#MILLISECONDS}）
     * @param <O> 最终结果类型
     * @return 执行结果
     */
    public <O> O getFutureResults(final ListenableFuture<O> allFutures, Long timeout, TimeUnit timeUnit) {
        try {
            if (timeout != null && timeUnit != null) {
                return allFutures.get(timeout, timeUnit);
            }
            if (timeout != null) {
                return allFutures.get(timeout, TimeUnit.MILLISECONDS);
            }
            return allFutures.get();
        } catch (final InterruptedException | ExecutionException ex) {
            // 其它异常信息，使用自定义异常进行包装
            throw new ExecuteException("execute task throw exception", ex);
        } catch (TimeoutException ex) {
            throw new ExecuteTimeoutException("execute task throw times out exception", ex);
        }
    }
}
