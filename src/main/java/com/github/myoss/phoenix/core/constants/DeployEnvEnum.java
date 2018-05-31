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

package com.github.myoss.phoenix.core.constants;

import static com.github.myoss.phoenix.core.constants.PhoenixConstants.DEPLOY_ENV;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务部署的环境变量
 *
 * @author Jerry.Chen
 * @since 2018年4月12日 下午2:04:43
 * @see PhoenixConstants#DEPLOY_ENV
 */
@AllArgsConstructor
public enum DeployEnvEnum {
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
    private static String deployEnv;

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
     * 是否为开发环境或者测试环境
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isDevOrTest(String value) {
        return DEV.getValue().equals(value) || TEST.getValue().equals(value);
    }

    /**
     * 是否为开发环境或者测试环境
     *
     * @return true: 是; false: 不是
     */
    public static boolean isDevOrTest() {
        String value = getDeployEnv();
        return DEV.getValue().equals(value) || TEST.getValue().equals(value);
    }

    /**
     * 是否为开发环境
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isDev(String value) {
        return DEV.getValue().equals(value);
    }

    /**
     * 是否为开发环境
     *
     * @return true: 是; false: 不是
     */
    public static boolean isDev() {
        String value = getDeployEnv();
        return DEV.getValue().equals(value);
    }

    /**
     * 是否为测试环境
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isTest(String value) {
        return TEST.getValue().equals(value);
    }

    /**
     * 是否为测试环境
     *
     * @return true: 是; false: 不是
     */
    public static boolean isTest() {
        String value = getDeployEnv();
        return TEST.getValue().equals(value);
    }

    /**
     * 是否为预发环境
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isPre(String value) {
        return PRE.getValue().equals(value);
    }

    /**
     * 是否为预发环境
     *
     * @return true: 是; false: 不是
     */
    public static boolean isPre() {
        String value = getDeployEnv();
        return PRE.getValue().equals(value);
    }

    /**
     * 是否为生产环境
     *
     * @param value 部署的环境变量
     * @return true: 是; false: 不是
     */
    public static boolean isPrd(String value) {
        return PRD.getValue().equals(value);
    }

    /**
     * 是否为生产环境
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
        if (deployEnv == null) {
            deployEnv = StringUtils.defaultIfBlank(System.getProperty(DEPLOY_ENV), System.getenv(DEPLOY_ENV));
        }
        return deployEnv;
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
}
