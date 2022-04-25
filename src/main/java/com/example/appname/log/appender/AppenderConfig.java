package com.example.appname.log.appender;

import ch.qos.logback.classic.LoggerContext;
import com.example.appname.log.appender.aws.kinesis.AWSKinesisAppender;
import com.example.appname.log.appender.logstash.LogstashAppender;
import com.example.appname.log.properties.LogProperties;
import com.example.appname.log.properties.LogProperties.Logstash;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppenderConfig {

    public AppenderConfig(@Value("${spring.application.name:_undefined}") final String appName,
                          @Value("${server.port:8080}") final String appPort,
                          @Value("${info.app.version:0}") final String version,
                          @Value("${spring.profiles.active:_undefined}") final String environment,
                          final LogProperties logProperties) {

        configLogstashAppenderIfItIsEnabled(appName, appPort, environment, version, logProperties);
        configKinesisAppenderIfItIsEnabled(appName, appPort, environment, version, logProperties);
    }

    private void configLogstashAppenderIfItIsEnabled(String appName,
                                                     String appPort,
                                                     String environment,
                                                     String version,
                                                     LogProperties logProperties) {
        final Logstash logstash = logProperties.getLogstash();
        if (logstash.isEnabled()) {
            final LogstashAppender logstashAppender =
                    new LogstashAppender(appName, appPort, environment, version, logProperties);
            logstashAppender.build((LoggerContext) LoggerFactory.getILoggerFactory());
        }
    }

    private void configKinesisAppenderIfItIsEnabled(String appName,
                                                    String appPort,
                                                    String environment,
                                                    String version,
                                                    LogProperties logProperties) {
        final LogProperties.Kinesis kinesis = logProperties.getKinesis();
        if (kinesis.isEnabled()) {
            final AWSKinesisAppender AWSKinesisAppender =
                    new AWSKinesisAppender(appName, appPort, environment, version, logProperties);
            AWSKinesisAppender.build((LoggerContext) LoggerFactory.getILoggerFactory());
        }
    }

    @Bean
    public LogProperties getLogProperties() {
        return new LogProperties();
    }

}
