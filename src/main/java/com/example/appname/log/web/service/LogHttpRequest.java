package com.example.appname.log.web.service;

import com.example.appname.log.util.LogFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@ConditionalOnWebApplication
public class LogHttpRequest {

    private static final String HEADER_USER_AGENT = "User-Agent";

    public void execute(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(">> REQUEST DATA: ");
        stringBuilder.append("method=[").append(request.getMethod()).append("], ");
        stringBuilder.append("uri=[").append(request.getRequestURI()).append("], ");
        stringBuilder.append("headers=[").append(buildRequestHeadersMap(request)).append("], ");

        Map<String, String> parameters = buildParametersMap(request);
        appendParameters(parameters, stringBuilder);

        log.info(stringBuilder.toString(),
                kv(LogFields.METHOD.getKey(), request.getMethod()),
                kv(LogFields.URI.getKey(), request.getRequestURI()),
                kv(LogFields.REQUEST_PARAMS.getKey(), parameters),
                kv(LogFields.USER_AGENT.getKey(), request.getHeader(HEADER_USER_AGENT)));
    }

    private Map<String, String> buildRequestHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            if ("Authorization".equalsIgnoreCase(key) && log.isDebugEnabled()) {
                map.put(key, value);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }
        return resultMap;
    }

    private void appendParameters(final Map<String, String> parameters, final StringBuilder stringBuilder) {
        if (!parameters.isEmpty()) {
            stringBuilder.append("requestParams=[").append(parameters).append("], ");
        }
    }

}
