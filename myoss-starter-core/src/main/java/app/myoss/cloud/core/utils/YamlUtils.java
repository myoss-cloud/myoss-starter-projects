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

package app.myoss.cloud.core.utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

/**
 * Yaml文件工具类
 *
 * @author Jerry.Chen
 * @since 2019年10月2日 下午3:46:34
 */
public class YamlUtils {
    private static final YamlPropertySourceLoader YAML_LOADER = new YamlPropertySourceLoader();

    /**
     * 读取 yaml 文件，转化为 Map
     *
     * @param path yaml 文件路径
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object> loadYaml2Map(Resource path) {
        if (!path.exists()) {
            return null;
        }

        PropertySource<Map<String, OriginTrackedValue>> propertySource;
        try {
            propertySource = (PropertySource<Map<String, OriginTrackedValue>>) YAML_LOADER.load("config", path).get(0);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load yaml configuration from " + path, ex);
        }

        return propertySource.getSource()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getValue(), (oldValue, newValue) -> newValue,
                        LinkedHashMap::new));
    }
}
