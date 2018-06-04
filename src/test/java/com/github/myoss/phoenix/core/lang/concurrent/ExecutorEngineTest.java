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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.util.ClassUtils;

import com.github.myoss.phoenix.core.exception.BizRuntimeException;
import com.google.common.collect.Lists;
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
    public OutputCapture output = new OutputCapture();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private final Pattern pattern = Pattern.compile("\\s*|\t|\r|\n|\r\n");

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
    public void executeTest3() throws IllegalAccessException, InterruptedException {
        ExecutorEngine executorEngine = new ExecutorEngine(Executors.newFixedThreadPool(3));
        ch.qos.logback.classic.Logger executorEngineLogger = (ch.qos.logback.classic.Logger) FieldUtils
                .readDeclaredField(executorEngine, "log", true);
        executorEngineLogger.setLevel(Level.ALL);
        List<Integer> inputs = Lists.newArrayList(1, 2, 3);
        List<Integer> result = executorEngine.execute(inputs, input -> {
            return Double.valueOf(Math.pow(input, 2)).intValue(); // 平方
        }, (MergeUnit<Integer, List<Integer>>) params -> {
            ArrayList<Integer> integers = Lists.newArrayList(params);
            Collections.sort(integers); // 排序
            return integers;
        });
        assertThat(result).isEqualTo(Lists.newArrayList(1, 4, 9));
        String printLog = this.output.toString();
        boolean flag = StringUtils.isBlank(pattern.matcher(printLog).replaceAll(""));
        int retry = 0;
        while (flag) { // 跑多线程，这个日志获取有点延时...
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
                return Double.valueOf(Math.pow(input, 2)).intValue(); // 平方
            }, (MergeUnit<Integer, List<Integer>>) params -> {
                ArrayList<Integer> integers = Lists.newArrayList(params);
                Collections.sort(integers); // 排序
                return integers;
            });
        } catch (Exception e) {
            delegate.shutdown();
            String printLog = this.output.toString();
            boolean flag = StringUtils.isBlank(pattern.matcher(printLog).replaceAll(""));
            int retry = 0;
            while (flag) { // 跑多线程，这个日志获取有点延时...
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
                    "" + ClassUtils.getQualifiedName(BizRuntimeException.class) + ": ba la ba la"
            );
            throw e;
        }
    }
}
