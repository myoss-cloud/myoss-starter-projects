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

package app.myoss.cloud.web.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.client.RestTemplate;

/**
 * Maven常用工具类方法
 *
 * @author Jerry.Chen
 * @since 2018年7月6日 下午3:41:19
 */
public class MavenUtils {
    /**
     * extract release version pattern
     */
    public static final Pattern RELEASE_PATTERN = Pattern.compile("<release>(\\S+)</release>");

    /**
     * 从 Nexus 仓库中获取 Maven 依赖最新的 release version
     *
     * @param restTemplate RestTemplate
     * @param nexusRepositoryUrl Nexus仓库地址url
     * @param artifactId Maven artifactId
     * @param groupId Maven groupId
     * @return 最新的 release version
     */
    public static String findReleaseVersionInNexus(RestTemplate restTemplate, String nexusRepositoryUrl,
                                                   String artifactId, String groupId) {
        String url = nexusRepositoryUrl + "/" + artifactId.replace(".", "/") + "/" + groupId + "/maven-metadata.xml";
        String forString = RestUtils.getForString(restTemplate, url);
        Matcher matcher = RELEASE_PATTERN.matcher(forString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
