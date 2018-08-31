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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;

import app.myoss.cloud.core.constants.MyossConstants;

/**
 * Fast Json的全局自动配置
 *
 * @author Jerry.Chen
 * @since 2018年4月12日 下午5:27:16
 */
@ConditionalOnClass({ FastJsonConfig.class })
@ConditionalOnWebApplication
@Configuration
public class FastJsonAutoConfiguration {
    /**
     * Fast Json的全局配置
     *
     * @return 默认启用 {@link SerializerFeature#DisableCircularReferenceDetect} 和
     *         {@link SerializerFeature#WriteMapNullValue}，并设置 charset 为 UTF-8
     */
    public static FastJsonConfig fastJsonConfig() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                // 关闭循环引用，输出的json字符串中有："$ref"
                SerializerFeature.DisableCircularReferenceDetect,
                // 将 null 字段输出字段名
                SerializerFeature.WriteMapNullValue);
        fastJsonConfig.setCharset(MyossConstants.DEFAULT_CHARSET);
        return fastJsonConfig;
    }

    /**
     * Fast Json的全局配置，使用 spring 管理，方便项目中替换或者获取此对象
     *
     * @return Fast Json的配置信息
     */
    @ConditionalOnMissingBean(name = "defaultFastJsonConfig")
    @Bean(name = "defaultFastJsonConfig")
    public FastJsonConfig defaultFastJsonConfig() {
        return fastJsonConfig();
    }
}
