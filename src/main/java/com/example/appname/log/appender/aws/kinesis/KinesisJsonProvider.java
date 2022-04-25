package com.example.appname.log.appender.aws.kinesis;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.example.appname.log.util.KeyValuePair;
import com.fasterxml.jackson.core.JsonGenerator;
import lombok.Builder;
import net.logstash.logback.composite.AbstractFieldJsonProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Builder
public class KinesisJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {
    public static final String DATE_TIME = "date_time";
    public static final String APP_NAME = "app_name";
    public static final String APP_PORT = "app_port";
    public static final String ENVIRONMENT = "environment";
    public static final String LEVEL = "level";
    public static final String LOGGER_NAME = "logger_name";
    public static final String MESSAGE = "message";
    public static final String PLATFORM = "platform";
    public static final String VERSION = "version";

    private final String appName;
    private final String appPort;
    private final String environment;
    private final String platform;
    private final String version;

    @Override
    public void writeTo(JsonGenerator jsonGenerator, ILoggingEvent event) throws IOException {
        List<KeyValuePair> keyValuePairs = new ArrayList<>();

        keyValuePairs.add(new KeyValuePair(DATE_TIME,
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(event.getTimeStamp()), TimeZone.getDefault().toZoneId()).toString()));
        keyValuePairs.add(new KeyValuePair(APP_NAME, appName));
        keyValuePairs.add(new KeyValuePair(APP_PORT, appPort));
        keyValuePairs.add(new KeyValuePair(ENVIRONMENT, environment));
        keyValuePairs.add(new KeyValuePair(LEVEL, event.getLevel().levelStr));
        keyValuePairs.add(new KeyValuePair(LOGGER_NAME, event.getLoggerName()));
        keyValuePairs.add(new KeyValuePair(MESSAGE, event.getFormattedMessage()));
        keyValuePairs.add(new KeyValuePair(PLATFORM, platform));
        keyValuePairs.add(new KeyValuePair(VERSION, version));

        if (event.getArgumentArray() != null) {
            keyValuePairs.addAll(Arrays.stream(event.getArgumentArray())
                    .map(obj -> KeyValuePair.valueOf(obj.toString()))
                    .collect(Collectors.toList()));
        }

        if (!event.getMDCPropertyMap().isEmpty()) {
            event.getMDCPropertyMap()
                    .entrySet()
                    .stream()
                    .map(obj -> new KeyValuePair(obj.getKey(), obj.getValue()))
                    .forEach(keyValuePairs::add);
        }

        writeKeyValueJson(jsonGenerator, keyValuePairs);
    }

    private void writeKeyValueJson(JsonGenerator jsonGenerator, List<KeyValuePair> arguments) throws IOException {
        for (KeyValuePair keyValuePair : arguments) {
            jsonGenerator.writeFieldName(keyValuePair.getKey());
            jsonGenerator.writeObject(keyValuePair.getValue());
        }
    }
}
