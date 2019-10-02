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

import java.util.List;

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Creates {@link LoadBalancerRequest}s for
 * {@link LoadBalancerClientRequestInterceptor}. Applies
 * {@link LoadBalancerRequestTransformer}s to the intercepted
 * {@link HttpRequest}.
 *
 * @author Jerry.Chen
 * @since 2019年9月9日 下午3:18:59
 * @see org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory
 */
public class LoadBalancerClientRequestFactory {
    private LoadBalancerClient                   loadBalancer;

    private List<LoadBalancerRequestTransformer> transformers;

    public LoadBalancerClientRequestFactory(LoadBalancerClient loadBalancer,
                                            List<LoadBalancerRequestTransformer> transformers) {
        this.loadBalancer = loadBalancer;
        this.transformers = transformers;
    }

    public LoadBalancerClientRequestFactory(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public LoadBalancerRequest<ClientHttpResponse> createRequest(final HttpRequest request, final byte[] body,
                                                                 final ClientHttpRequestExecution execution) {
        return instance -> {
            HttpRequest serviceRequest = new ServiceRequestWrapper(request, instance, this.loadBalancer);
            if (this.transformers != null) {
                for (LoadBalancerRequestTransformer transformer : this.transformers) {
                    serviceRequest = transformer.transformRequest(serviceRequest, instance);
                }
            }
            return execution.execute(serviceRequest, body);
        };
    }
}
