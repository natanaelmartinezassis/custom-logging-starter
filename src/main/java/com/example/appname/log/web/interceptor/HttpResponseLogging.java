package com.example.appname.log.web.interceptor;

import com.example.appname.log.web.service.LogHttpResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@AllArgsConstructor
@ConditionalOnWebApplication
public class HttpResponseLogging implements ResponseBodyAdvice<Object> {

    private LogHttpResponse logHttpResponse;

    @Override
    public boolean supports(final MethodParameter returnType,
                            final Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(final Object body,
                                  final MethodParameter returnType,
                                  final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  final ServerHttpRequest request,
                                  final ServerHttpResponse response) {

        if (request instanceof ServletServerHttpRequest && response instanceof ServletServerHttpResponse) {
            logHttpResponse.execute(
                    ((ServletServerHttpRequest) request).getServletRequest(), ((ServletServerHttpResponse) response)
                            .getServletResponse(), body);
        }
        return body;
    }

}
