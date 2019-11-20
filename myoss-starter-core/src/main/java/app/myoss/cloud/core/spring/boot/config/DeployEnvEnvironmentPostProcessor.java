/*
 * Copyright 2018-2019 https://github.com/myoss
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import app.myoss.cloud.core.constants.DeployEnvEnum;
import app.myoss.cloud.core.constants.MyossConstants;
import app.myoss.cloud.core.utils.YamlUtils;

/**
 * 加载"服务部署的环境变量"，保存到 spring {@link org.springframework.core.env.Environment} 中
 * <p>
 * 如果需要启用这个功能，请按下面的步骤操作：
 * <ul>
 * <li>可以在某个核心 maven依赖中，增加配置文件信息：resources:core-config/service-deploy-env.yml
 * <li>service-deploy-env.yml 中增加配置项：DEPLOY_ENV_CUSTOMIZE_DEV: local
 * </ul>
 *
 * @author Jerry.Chen
 * @since 2019年10月2日 下午3:26:17
 */
public class DeployEnvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered, SmartApplicationListener {
    /**
     * {@link EnvironmentPostProcessor} 中比较特殊，不能直接用 @Slf4j 进行输出日志
     */
    private static final DeferredLog LOGGER        = new DeferredLog();
    /**
     * The default order for the processor.
     */
    public static final int          DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 3;

    private int                      order         = DEFAULT_ORDER;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 加载 "服务部署的环境变量" 的配置文件
        Resource path = new ClassPathResource("core-config/service-deploy-env.yml");
        LinkedHashMap<String, Object> linkedHashMap = YamlUtils.loadYaml2Map(path);
        if (linkedHashMap != null) {
            String customizeDev = (String) linkedHashMap.get(DeployEnvEnum.CUSTOMIZE_DEV.getValue());
            if (StringUtils.isNotBlank(customizeDev)) {
                // 自定义开发环境, 实际业务场景中: 不是用 dev 代表开发环境
                LOGGER.info("DEPLOY_ENV_CUSTOMIZE_DEV set to " + customizeDev);
                System.setProperty(DeployEnvEnum.CUSTOMIZE_DEV.getValue(), customizeDev);

                // 加载当前环境的配置文件，进行覆盖
                String deployEnv = DeployEnvEnum.getDeployEnv();
                if (deployEnv == null) {
                    // 跑 test case 的时候，可以考虑加下这个： @SpringBootTest(properties = {"DEPLOY_ENV:test"})
                    deployEnv = environment.getProperty(MyossConstants.DEPLOY_ENV);
                }
                if (deployEnv == null) {
                    LOGGER.info("DEPLOY_ENV set to " + customizeDev);
                    DeployEnvEnum.setDeployEnv(customizeDev);
                }
            }
            CoreCommonEnvironmentPostProcessor.addOrReplace(environment.getPropertySources(), linkedHashMap);
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
            LOGGER.replayTo(DeployEnvEnvironmentPostProcessor.class);
        }
    }

    @Override
    public int getOrder() {
        return order;
    }
}
