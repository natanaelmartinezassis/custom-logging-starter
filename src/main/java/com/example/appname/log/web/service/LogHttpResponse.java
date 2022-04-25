package com.example.appname.log.web.service;

import com.example.appname.log.util.LogFields;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@ConditionalOnWebApplication
public class LogHttpResponse {

    private static final String HEADER_USER_AGENT = "User-Agent";

    public void execute(final HttpServletRequest request, final HttpServletResponse response, final Object body) {
        final LocalDateTime execStart = (LocalDateTime) request.getAttribute(LogFields.EXEC_START.getKey());
        final LocalDateTime execEnd = now();
        final long elapsedTime = millisecondsBetween(execStart, execEnd);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<< RESPONSE DATA: ");
        stringBuilder.append("method=[").append(request.getMethod()).append("], ");
        stringBuilder.append("uri=[").append(request.getRequestURI()).append("], ");
        appendResponseHeader(stringBuilder, response);
        stringBuilder.append("statusCode=[").append(response.getStatus()).append("], ");
        stringBuilder.append("elapsedTime=[").append(elapsedTime).append(" millis], ");

        log.info(stringBuilder.toString(),
                kv(LogFields.PAYLOAD.getKey(), getPayload(body)),
                kv(LogFields.METHOD.getKey(), request.getMethod()),
                kv(LogFields.URI.getKey(), request.getRequestURI()),
                kv(LogFields.USER_AGENT.getKey(), request.getHeader(HEADER_USER_AGENT)),
                kv(LogFields.STATUS_CODE.getKey(), response.getStatus()),
                kv(LogFields.EXEC_START.getKey(), execStart != null ? execStart.toString() : -1),
                kv(LogFields.EXEC_END.getKey(), execEnd.toString()),
                kv(LogFields.ELAPSED_TIME_IN_MILLIS.getKey(), elapsedTime));
    }

    private long millisecondsBetween(final LocalDateTime execStart, final LocalDateTime execEnd) {
        if (execStart != null) {
            return Duration.between(execStart, execEnd).toMillis();
        }
        return -1;
    }

    private void appendResponseHeader(final StringBuilder stringBuilder, final HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        response.getHeaderNames().forEach(header -> map.put(header, response.getHeader(header)));
        if (!map.isEmpty()) {
            stringBuilder.append("responseHeaders=[").append(map).append("], ");
        }
    }

    private String getPayload(final Object body) {
        if (body != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                log.error("Error to write object as json string", e);
            }
        }
        return "";
    }

}
