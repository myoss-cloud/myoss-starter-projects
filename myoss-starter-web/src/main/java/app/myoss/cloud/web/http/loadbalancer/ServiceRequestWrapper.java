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

import java.net.URI;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 扩展 {@link HttpRequestWrapper}，在发送请求之前，从服务发现中心，解析 lb:// 协议地址
 *
 * @author Jerry.Chen
 * @since 2019年9月9日 下午3:20:10
 * @see org.springframework.cloud.client.loadbalancer.ServiceRequestWrapper
 */
@Slf4j
public class ServiceRequestWrapper extends HttpRequestWrapper {

    private final ServiceInstance    instance;

    private final LoadBalancerClient loadBalancer;

    public ServiceRequestWrapper(HttpRequest request, ServiceInstance instance, LoadBalancerClient loadBalancer) {
        super(request);
        this.instance = instance;
        this.loadBalancer = loadBalancer;
    }

    @Override
    public URI getURI() {
        URI originUri = getRequest().getURI();
        log.trace("url before: {}", originUri);
        URI toUri = originUri;
        if ("lb".equals(originUri.getScheme())) {
            toUri = UriComponentsBuilder.fromUri(originUri).scheme("http").build().toUri();
        }
        URI uri = this.loadBalancer.reconstructURI(this.instance, toUri);
        log.trace("url chosen: {}", uri);
        return uri;
    }
}
