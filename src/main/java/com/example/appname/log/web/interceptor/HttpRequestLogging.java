package com.example.appname.log.web.interceptor;

import com.example.appname.log.util.LogFields;
import com.example.appname.log.web.service.LogHttpRequest;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.time.LocalDateTime.now;

@Component
@AllArgsConstructor
@ConditionalOnWebApplication
public class HttpRequestLogging implements HandlerInterceptor {

    private LogHttpRequest logHttpRequest;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {
        request.setAttribute(LogFields.EXEC_START.getKey(), now());
        logHttpRequest.execute(request);
        return true;
    }

}
