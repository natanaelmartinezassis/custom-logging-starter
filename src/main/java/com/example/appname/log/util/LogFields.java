package com.example.appname.log.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogFields {

    CLASS("class"),
    ELAPSED_TIME_IN_MILLIS("elapsed_time_in_millis"),
    ERROR_CODE("error_code"),
    ERROR_MESSAGE("error_message"),
    EXCEPTION("exception"),
    EXEC_END("exec_end"),
    EXEC_START("exec_start"),
    METHOD("method"),
    PAYLOAD("payload"),
    REQUEST_PARAMS("request_params"),
    STATUS_CODE("status_code"),
    TRACE_ID("traceId"),
    SPAN_ID("spanId"),
    USER_AGENT("user_agent"),
    URI("uri");

    private final String key;

}
