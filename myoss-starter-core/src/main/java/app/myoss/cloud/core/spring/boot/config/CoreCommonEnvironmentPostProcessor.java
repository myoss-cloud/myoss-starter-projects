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

package app.myoss.cloud.core.spring.boot.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import app.myoss.cloud.core.constants.DeployEnvEnum;
import app.myoss.cloud.core.constants.MyossConstants;
import app.myoss.cloud.core.utils.YamlUtils;

/**
 * 加载核心自定义基础配置信息，保存到 spring {@link org.springframework.core.env.Environment} 中
 * <p>
 * 如果需要启用这个功能，可以在某个核心 maven
 * 依赖中，增加不同的配置文件信息；resources:core-config/*.yml，它的优先级低于项目中的 application.yml :
 * <ul>
 * <li>core-config/application.yml
 * <li>core-config/application-dev.yml
 * <li>core-config/application-test.yml
 * <li>core-config/application-pre.yml
 * <li>core-config/application-prd.yml
 * </ul>
 *
 * @author Jerry.Chen
 * @since 2018年11月22日 下午2:16:55
 */
public class CoreCommonEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered, SmartApplicationListener {
    /**
     * {@link EnvironmentPostProcessor} 中比较特殊，不能直接用 @Slf4j 进行输出日志
     */
    private static final DeferredLog LOGGER               = new DeferredLog();
    /**
     * The default order for the processor.
     */
    public static final int          DEFAULT_ORDER        = ConfigFileApplicationListener.DEFAULT_ORDER + 9;
    /**
     * 属性配置名字
     */
    public static final String       PROPERTY_SOURCE_NAME = "defaultProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 加载默认的配置文件
        Resource path = new ClassPathResource("core-config/application.yml");
        LinkedHashMap<String, Object> linkedHashMap = YamlUtils.loadYaml2Map(path);
        addOrReplace(environment.getPropertySources(), linkedHashMap);

        // 加载当前环境的配置文件，进行覆盖
        String deployEnv = DeployEnvEnum.getDeployEnv();
        if (deployEnv == null) {
            // 跑 test case 的时候，可以考虑加下这个： @SpringBootTest(properties = {"DEPLOY_ENV:test"})
            deployEnv = environment.getProperty(MyossConstants.DEPLOY_ENV);
        }
        path = new ClassPathResource("core-config/application-" + deployEnv + ".yml");
        linkedHashMap = YamlUtils.loadYaml2Map(path);
        addOrReplace(environment.getPropertySources(), linkedHashMap);
    }

    /**
     * 增加或者替换配置属性：{@link #PROPERTY_SOURCE_NAME}
     *
     * @param propertySources
     *            {@link ConfigurableEnvironment#getPropertySources()}
     * @param map 新配置的属性
     */
    public static void addOrReplace(MutablePropertySources propertySources, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        MapPropertySource target = null;
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                target.getSource().putAll(map);
            }
        }
        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }
        if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.addLast(target);
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationPreparedEvent) {
            LOGGER.replayTo(CoreCommonEnvironmentPostProcessor.class);
        }
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
