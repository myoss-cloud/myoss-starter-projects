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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.util.ClassUtils;

import com.google.common.collect.Lists;

import app.myoss.cloud.core.exception.BizRuntimeException;
import app.myoss.cloud.core.utils.EmojiUtils;
import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ExecutorEngine} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年6月5日 上午12:13:37
 */
@Slf4j
public class ExecutorEngineTest {
    @Rule
    public OutputCapture     output  = new OutputCapture();
    @Rule
    public ExpectedException thrown  = ExpectedException.none();
    private final Pattern    pattern = Pattern.compile("\\s*|\t|\r|\n|\r\n");

    @Test
    public void executeTest1() {
        ExecutorEngine executorEngine = ExecutorEngine.buildTreadPoolExecutor();
        List<Integer> inputs = Lists.newArrayList(1, 2, 3);
        Long sleepTime = 500L;
        int totalCostTime = 0;
        int runCount = 5;
        for (int i = 0; i < runCount; i++) {
            long start = System.currentTimeMillis();
            List<Object> execute = executorEngine.execute(inputs, input -> {
                Thread.sleep(sleepTime);
                log.info("input -->> {}", input);
                return input + 10;
            });
            long end = System.currentTimeMillis();
            long costTime = end - start;
            totalCostTime += costTime;
            log.info("[test " + (i + 1) + "] cost time: " + costTime);
            for (Object o : execute) {
                log.info("[test " + (i + 1) + "] " + o);
            }
            log.info("");
        }
        int avgCostTime = totalCostTime / runCount;
        log.info("总耗时: {}, 总共运行: {}次, 平均耗时: {}", totalCostTime, runCount, avgCostTime);
        assertThat(avgCostTime).isLessThan(sleepTime.intValue() + 100);
    }

    @Test
    public void executeTest2() {
        ExecutorEngine executorEngine = ExecutorEngine.buildTreadPoolExecutor();
        List<Integer> inputs = Lists.newArrayList(1, 2, 3);
        Long sleepTime = 500L;
        int totalCostTime = 0;
        int runCount = 5;
        for (int i = 0; i < runCount; i++) {
            long start = System.currentTimeMillis();
            TimeUnit timeUnit = (i % 2 == 0 ? TimeUnit.MILLISECONDS : null);
            List<Object> execute = executorEngine.execute(inputs, input -> {
                Thread.sleep(sleepTime);
                log.info("input -->> {}", input);
                return input + 10;
            }, sleepTime + 10, timeUnit);
            // 因为是并发去执行，线程足够多的时候，全部执行下来，只需要花费单个执行的时间（无限接近），所以这里设置总的超时时间为：500L + 10L = 510L
            long end = System.currentTimeMillis();
            long costTime = end - start;
            totalCostTime += costTime;
            log.info("[test " + (i + 1) + "] cost time: " + costTime);
            for (Object o : execute) {
                log.info("[test " + (i + 1) + "] " + o);
            }
            log.info("");
        }
        int avgCostTime = totalCostTime / runCount;
        log.info("总耗时: {}, 总共运行: {}次, 平均耗时: {}, 校验多线程执行不超过: {}", totalCostTime, runCount, avgCostTime, sleepTime + 10);
        assertThat(avgCostTime).isLessThan(sleepTime.intValue() + 100);
    }

    @Test
    @SuppressWarnings("checkstyle:Indentation")
    public void executeTest3() throws IllegalAccessException, InterruptedException {
        ExecutorEngine executorEngine = new ExecutorEngine(Executors.newFixedThreadPool(3));
        ch.qos.logback.classic.Logger executorEngineLogger = (ch.qos.logback.classic.Logger) FieldUtils
                .readDeclaredField(executorEngine, "log", true);
        executorEngineLogger.setLevel(Level.ALL);
        List<Integer> inputs = Lists.newArrayList(1, 2, 3);
        List<Integer> result = executorEngine.execute(inputs, input -> {
            // 平方
            return Double.valueOf(Math.pow(input, 2)).intValue();
        }, (MergeUnit<Integer, List<Integer>>) params -> {
            ArrayList<Integer> integers = Lists.newArrayList(params);
            // 排序
            Collections.sort(integers);
            return integers;
        });
        assertThat(result).isEqualTo(Lists.newArrayList(1, 4, 9));
        String printLog = this.output.toString();
        boolean flag = StringUtils.isBlank(pattern.matcher(printLog).replaceAll(""));
        int retry = 0;
        while (flag) {
            // 跑多线程，这个日志获取有点延时...
            printLog = this.output.toString();
            flag = StringUtils.isBlank(pattern.matcher(printLog).replaceAll(""));
            if (retry == 20) {
                break;
            }
            Thread.sleep(200L);
            retry++;
        }
        log.warn(
                "\n<<<<<<<<========= printLog start =========>>>>>>>> \n{}<<<<<<<<========= printLog end =========>>>>>>>>\n",
                printLog);
        assertThat(printLog).contains(" TRACE ", " " + ClassUtils.getQualifiedName(ExecutorEngine.class) + " ",
                "Concurrent execute result success ");
    }

    @Test
    @SuppressWarnings("checkstyle:Indentation")
    public void executeTest4() throws InterruptedException {
        ExecutorService delegate = Executors.newFixedThreadPool(3);
        ExecutorEngine executorEngine = new ExecutorEngine(delegate);
        List<Integer> inputs = Lists.newArrayList(1, 2, 3);
        thrown.expect(ExecuteException.class);
        thrown.expectMessage("execute task throw exception");
        try {
            executorEngine.execute(inputs, input -> {
                if (input == 2) {
                    throw new BizRuntimeException("ba la ba la");
                }
                // 平方
                return Double.valueOf(Math.pow(input, 2)).intValue();
            }, (MergeUnit<Integer, List<Integer>>) params -> {
                ArrayList<Integer> integers = Lists.newArrayList(params);
                // 排序
                Collections.sort(integers);
                return integers;
            });
        } catch (Exception e) {
            String printLog = this.output.toString();
            boolean flag = StringUtils.isBlank(pattern.matcher(printLog).replaceAll(""));
            int retry = 0;
            while (flag) {
                // 跑多线程，这个日志获取有点延时...
                printLog = this.output.toString();
                flag = StringUtils.isBlank(pattern.matcher(printLog).replaceAll(""));
                if (retry == 20) {
                    break;
                }
                Thread.sleep(200L);
                retry++;
            }
            log.warn(
                    "\n<<<<<<<<========= printLog start =========>>>>>>>> \n{}<<<<<<<<========= printLog end =========>>>>>>>>\n",
                    printLog);
            assertThat(printLog).contains(" WARN ", " " + ClassUtils.getQualifiedName(ExecutorEngine.class) + " ",
                    "Concurrent execute result failure",
                    "" + ClassUtils.getQualifiedName(BizRuntimeException.class) + ": ba la ba la");
            throw e;
        }
    }

    @Test
    public void executeTest5() {
        ExecutorService delegate = Executors.newFixedThreadPool(3);
        ExecutorEngine executorEngine = new ExecutorEngine(delegate);
        String param = "风青杨\uD83D\uDE0D";
        List<ExecuteUnit<String, String>> executeUnits = new ArrayList<>();
        executeUnits.add(EmojiUtils::addBackslash);
        executeUnits.add(EmojiUtils::removeBackslash);
        Long sleepTime = 500L;
        int totalCostTime = 0;
        int runCount = 5;
        for (int i = 0; i < runCount; i++) {
            long start = System.currentTimeMillis();
            TimeUnit timeUnit = (i % 2 == 0 ? TimeUnit.MILLISECONDS : null);
            Long timeout = (i % 2 == 1 ? 10L : null);
            List<String> execute = executorEngine.execute(param, executeUnits, timeout, timeUnit);
            long end = System.currentTimeMillis();
            long costTime = end - start;
            totalCostTime += costTime;
            log.info("[test " + (i + 1) + "] cost time: " + costTime);
            for (Object o : execute) {
                log.info("[test " + (i + 1) + "] " + o);
            }
            log.info("");
        }
        int avgCostTime = totalCostTime / runCount;
        log.info("总耗时: {}, 总共运行: {}次, 平均耗时: {}", totalCostTime, runCount, avgCostTime);
        assertThat(avgCostTime).isLessThan(sleepTime.intValue() + 100);
    }
}
