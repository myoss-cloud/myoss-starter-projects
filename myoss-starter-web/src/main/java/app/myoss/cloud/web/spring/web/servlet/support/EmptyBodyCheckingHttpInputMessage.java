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

package app.myoss.cloud.web.spring.web.servlet.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.util.StreamUtils;

/**
 * 检查HTTP input message 的 {@linkplain #getBody()}，如果body中没有值，则返回{@code null}
 *
 * @author Jerry.Chen
 * @since 2018年4月11日 下午3:00:24
 */
public class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {
    private final HttpHeaders headers;
    private final InputStream body;
    private final HttpMethod  method;

    /**
     * 检查HTTP input message 的 {@linkplain #getBody()}，如果body中没有值，则返回{@code null}
     *
     * @param inputMessage an HTTP input message
     * @throws IOException IO异常
     */
    public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
        this.headers = inputMessage.getHeaders();
        InputStream inputStream = inputMessage.getBody();
        if (inputStream.markSupported()) {
            inputStream.mark(1);
            this.body = (inputStream.read() != -1 ? inputStream : null);
            inputStream.reset();
        } else {
            PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
            int b = pushbackInputStream.read();
            if (b == -1) {
                this.body = null;
            } else {
                this.body = pushbackInputStream;
                pushbackInputStream.unread(b);
            }
        }
        this.method = ((HttpRequest) inputMessage).getMethod();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public InputStream getBody() {
        return (this.body != null ? this.body : StreamUtils.emptyInput());
    }

    /**
     * 判断请求 body 是否有值
     *
     * @return true/false
     */
    public boolean hasBody() {
        return (this.body != null);
    }

    /**
     * HTTP 请求方法
     *
     * @return HTTP 请求方法
     */
    public HttpMethod getMethod() {
        return this.method;
    }
}
