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

package app.myoss.cloud.web.http.loadbalancer;

import java.io.IOException;
import java.net.URI;

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Intercepts client-side HTTP requests: 在发送请求之前，从服务发现中心，解析 lb:// 协议地址
 *
 * @author Jerry.Chen
 * @since 2019年9月9日 上午11:57:56
 */
@Slf4j
@AllArgsConstructor
public class LoadBalancerClientRequestInterceptor implements ClientHttpRequestInterceptor {
    protected final LoadBalancerClient               loadBalancer;
    protected final LoadBalancerClientRequestFactory requestFactory;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        URI originalUri = request.getURI();
        if (!"lb".equals(originalUri.getScheme())) {
            return execution.execute(request, body);
        }

        String serviceName = originalUri.getHost();
        Assert.state(serviceName != null, "Request URI does not contain a valid hostname: " + originalUri);
        return loadBalancer.execute(serviceName, requestFactory.createRequest(request, body, execution));
    }
}
