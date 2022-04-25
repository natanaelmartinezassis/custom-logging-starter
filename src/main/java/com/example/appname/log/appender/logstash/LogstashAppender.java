package com.example.appname.log.appender.logstash;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import com.example.appname.log.appender.AbstractAppender;
import com.example.appname.log.properties.LogProperties;
import lombok.AllArgsConstructor;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.springframework.util.ObjectUtils;

import java.net.InetSocketAddress;

@AllArgsConstructor
public class LogstashAppender extends AbstractAppender {

    private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH_APPENDER";
    private static final String ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH_APPENDER";

    private final String appName;
    private final String appPort;
    private final String environment;
    private final String version;
    private final LogProperties logProperties;

    @Override
    public void build(final LoggerContext context) {
        final LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
        logstashAppender.setName(LOGSTASH_APPENDER_NAME);
        logstashAppender.setContext(context);
        logstashAppender.addDestinations(new InetSocketAddress(logProperties.getLogstash().getHost(),
                logProperties.getLogstash().getPort()));
        setEncoder(logstashAppender);
        logstashAppender.start();

        final AsyncAppender asyncLogstashAppender = new AsyncAppender();
        asyncLogstashAppender.setName(ASYNC_LOGSTASH_APPENDER_NAME);
        asyncLogstashAppender.setContext(context);
        asyncLogstashAppender.setQueueSize(logProperties.getLogstash().getQueueSize());
        asyncLogstashAppender.addAppender(logstashAppender);
        asyncLogstashAppender.start();

        setAppenderToLogger(logProperties.getDefaultPackage(),
                logProperties.getLevel(),
                false,
                asyncLogstashAppender,
                context);

        logProperties.getExtraPackages()
                .stream()
                .filter(packageInfo -> !ObjectUtils.isEmpty(packageInfo.getName()))
                .forEach(packageInfo -> setAppenderToLogger(packageInfo.getName(),
                        packageInfo.getLevel(),
                        false,
                        asyncLogstashAppender,
                        context));

        addContextListener(context);
    }

    private void setEncoder(LogstashTcpSocketAppender logstashAppender) {
        final String sb = "{" +
                "\"app_name\":\"%s\", " +
                "\"app_port\":\"%s\", " +
                "\"environment\":\"%s\", " +
                "\"platform\":\"%s\", " +
                "\"version\":\"%s\"" +
                "}";
        final String customFields =
                String.format(sb, appName, appPort, environment, logProperties.getPlatform(), version);

        final LogstashEncoder logstashEncoder = new LogstashEncoder();
        logstashEncoder.setCustomFields(customFields);

        final ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
        throwableConverter.setRootCauseFirst(true);
        logstashEncoder.setThrowableConverter(throwableConverter);

        logstashAppender.setEncoder(logstashEncoder);
    }

    @Override
    public AbstractAppender getAbstractAppender() {
        return this;
    }

}
