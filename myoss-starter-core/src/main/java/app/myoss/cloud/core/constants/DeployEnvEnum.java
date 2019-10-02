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

package app.myoss.cloud.core.constants;

import static app.myoss.cloud.core.constants.MyossConstants.DEPLOY_ENV;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务部署的环境变量
 *
 * @author Jerry.Chen
 * @since 2018年4月12日 下午2:04:43
 * @see MyossConstants#DEPLOY_ENV
 */
@AllArgsConstructor
public enum DeployEnvEnum {
    /**
     * 自定义本地开发环境: 运行在办公室开发者的电脑中
     */
    CUSTOMIZE_DEV("DEPLOY_ENV_CUSTOMIZE_DEV", "自定义本地开发环境"),
    /**
     * 本地开发环境: 运行在办公室开发者的电脑中
     */
    LOCAL("local", "本地开发环境"),
    /**
     * 开发环境
     */
    DEV("dev", "开发环境"),
    /**
     * 开发环境
     */
    TEST("test", "测试环境"),
    /**
     * 预发环境
     */
    PRE("pre", "预发环境"),
    /**
     * 生产环境
     */
    PRD("prd", "生产环境");

    @Getter
    private String        value;
    @Getter
    private String        name;
    private static String CURRENT_ENV;

    /**
     * 根据环境变量的值获取枚举
     *
     * @param value 环境变量的值
     * @return 枚举
     */
    public static DeployEnvEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (DeployEnvEnum item : DeployEnvEnum.values()) {
            if (Objects.equals(item.getValue(), value)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 是否为自定义开发环境, 实际业务场景中: 不是用 dev 代表开发环境
     *
     * @return true: 是; false: 不是
     * @see app.myoss.cloud.core.spring.boot.config.DeployEnvEnvironmentPostProcessor
     *      请参考文档进行设置
     */
    public static boolean isCustomizeDev() {
        String value = getDeployEnv();
        String customizeDev = StringUtils.defaultIfBlank(System.getProperty(CUSTOMIZE_DEV.getValue()),
                System.getenv(CUSTOMIZE_DEV.getValue()));
        if (StringUtils.isNotBlank(customizeDev)) {
            return StringUtils.equals(value, customizeDev);
        } else {
            return isDev(value);
        }
    }

    /**
     * 是否为本地开发环境: DEPLOY_ENV=local
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isLocal(String value) {
        return LOCAL.getValue().equals(value);
    }

    /**
     * 是否为开发环境: DEPLOY_ENV=local
     *
     * @return true: 是; false: 不是
     */
    public static boolean isLocal() {
        String value = getDeployEnv();
        return LOCAL.getValue().equals(value);
    }

    /**
     * 是否为开发环境或者测试环境: dev or test
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isDevOrTest(String value) {
        return DEV.getValue().equals(value) || TEST.getValue().equals(value);
    }

    /**
     * 是否为开发环境或者测试环境: dev or test
     *
     * @return true: 是; false: 不是
     */
    public static boolean isDevOrTest() {
        String value = getDeployEnv();
        return DEV.getValue().equals(value) || TEST.getValue().equals(value);
    }

    /**
     * 是否为开发环境: DEPLOY_ENV=dev
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isDev(String value) {
        return DEV.getValue().equals(value);
    }

    /**
     * 是否为开发环境: DEPLOY_ENV=dev
     *
     * @return true: 是; false: 不是
     */
    public static boolean isDev() {
        String value = getDeployEnv();
        return DEV.getValue().equals(value);
    }

    /**
     * 是否为测试环境: DEPLOY_ENV=test
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isTest(String value) {
        return TEST.getValue().equals(value);
    }

    /**
     * 是否为测试环境: DEPLOY_ENV=test
     *
     * @return true: 是; false: 不是
     */
    public static boolean isTest() {
        String value = getDeployEnv();
        return TEST.getValue().equals(value);
    }

    /**
     * 是否为预发环境: DEPLOY_ENV=pre
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isPre(String value) {
        return PRE.getValue().equals(value);
    }

    /**
     * 是否为预发环境: DEPLOY_ENV==pre
     *
     * @return true: 是; false: 不是
     */
    public static boolean isPre() {
        String value = getDeployEnv();
        return PRE.getValue().equals(value);
    }

    /**
     * 是否为生产环境: DEPLOY_ENV=prd
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isPrd(String value) {
        return PRD.getValue().equals(value);
    }

    /**
     * 是否为生产环境: DEPLOY_ENV=prd
     *
     * @return true: 是; false: 不是
     */
    public static boolean isPrd() {
        String value = getDeployEnv();
        return PRD.getValue().equals(value);
    }

    /**
     * 获取应用部署的环境变量
     *
     * @return 部署的环境变量
     */
    public static String getDeployEnv() {
        if (CURRENT_ENV == null) {
            CURRENT_ENV = StringUtils.defaultIfBlank(System.getProperty(DEPLOY_ENV), System.getenv(DEPLOY_ENV));
        }
        return CURRENT_ENV;
    }

    /**
     * 获取应用部署的环境变量
     *
     * @return 部署的环境变量
     * @see #getDeployEnv()
     */
    public static DeployEnvEnum getDeployEnvEnum() {
        String deployEnv = getDeployEnv();
        return getEnumByValue(deployEnv);
    }

    /**
     * 设置应用部署的环境变量
     *
     * @param deployEnv 部署的环境变量
     */
    public static void setDeployEnv(String deployEnv) {
        System.setProperty(DEPLOY_ENV, deployEnv);
    }
}
