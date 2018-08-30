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

import java.nio.charset.Charset;

/**
 * 项目常量
 *
 * @author Jerry.Chen
 * @since 2018年4月11日 下午3:08:26
 */
public interface PhoenixConstants {
    /**
     * 获取应用部署的环境变量
     *
     * @return 部署的环境变量
     */
    default String getDeployEnv() {
        return DeployEnvEnum.getDeployEnv();
    }

    /**
     * UTF-8: 默认的字符集
     */
    Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    /**
     * 部署的环境变量（dev,test,pre,prd）
     *
     * @see DeployEnvEnum
     */
    String  DEPLOY_ENV      = "DEPLOY_ENV";

    /**
     * 下划线
     */
    String  UNDERLINE       = "_";
    /**
     * 字符串常量: Y
     */
    String  Y               = "Y";
    /**
     * 字符串常量: N
     */
    String  N               = "N";
    /**
     * 字符串常量: system
     */
    String  SYSTEM          = "system";
}
