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

package app.myoss.cloud.web.constants;

import app.myoss.cloud.core.constants.MyossConstants;

/**
 * Web常量
 *
 * @author Jerry.Chen
 * @since 2018年12月17日 下午4:15:33
 */
public class WebConstants {
    /**
     * 缓存配置前缀
     */
    public static final String CONFIG_PREFIX                     = MyossConstants.CONFIG_PREFIX + ".web";
    /**
     * OkHttp3连接池属性配置前缀
     */
    public static final String OK_HTTP3_CONNECTION_CONFIG_PREFIX = MyossConstants.CONFIG_PREFIX
            + ".ok-http3.connection-pool";

    /**
     * restTemplate4OkHttp3 spring bean name
     */
    public static final String REST_TEMPLATE4_OK_HTTP3_BEAN_NAME = "restTemplate4OkHttp3";
}
