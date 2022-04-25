package com.example.appname.log.appender.aws.kinesis;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.example.appname.log.appender.AbstractAppender;
import com.example.appname.log.properties.LogProperties;
import com.gu.logback.appender.kinesis.KinesisAppender;
import lombok.RequiredArgsConstructor;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.layout.LoggingEventCompositeJsonLayout;
import org.springframework.util.ObjectUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@RequiredArgsConstructor
public class AWSKinesisAppender extends AbstractAppender {

    private static final String KINESIS_APPENDER_NAME = "KINESIS_APPENDER";
    private static final String ASYNC_KINESIS_APPENDER_NAME = "ASYNC_KINESIS_APPENDER";

    private final String appName;
    private final String appPort;
    private final String environment;
    private final String version;
    private final LogProperties logProperties;

    @Override
    public void build(final LoggerContext context) {
        LogProperties.Kinesis properties = logProperties.getKinesis();

        final KinesisAppender<ILoggingEvent> kinesisAppender = new KinesisAppender<>();
        kinesisAppender.setName(KINESIS_APPENDER_NAME);
        kinesisAppender.setContext(context);
        kinesisAppender.setBufferSize(properties.getBufferSize());
        kinesisAppender.setThreadCount(properties.getThreadCount());
        kinesisAppender.setEndpoint(properties.getEndpoint());
        if (properties.getEndpoint().isEmpty()) {
            kinesisAppender.setRegion(properties.getRegion());
        }
        kinesisAppender.setMaxRetries(properties.getMaxRetries());
        kinesisAppender.setShutdownTimeout(properties.getShutdownTimeout());
        kinesisAppender.setStreamName(properties.getStreamName());
        kinesisAppender.setEncoding(properties.getEncoding());
        setCredentials(kinesisAppender);
        setJsonLayout(kinesisAppender);
        kinesisAppender.start();

        final AsyncAppender asyncKinesisAppender = new AsyncAppender();
        asyncKinesisAppender.setName(ASYNC_KINESIS_APPENDER_NAME);
        asyncKinesisAppender.setContext(context);
        asyncKinesisAppender.setQueueSize(logProperties.getLogstash().getQueueSize());
        asyncKinesisAppender.addAppender(kinesisAppender);
        asyncKinesisAppender.start();

        setAppenderToLogger(logProperties.getDefaultPackage(),
                logProperties.getLevel(),
                false,
                asyncKinesisAppender,
                context);

        logProperties.getExtraPackages()
                .stream()
                .filter(packageInfo -> !ObjectUtils.isEmpty(packageInfo.getName()))
                .forEach(packageInfo -> setAppenderToLogger(packageInfo.getName(),
                        packageInfo.getLevel(),
                        false,
                        asyncKinesisAppender,
                        context));

        addContextListener(context);
    }

    private void setCredentials(KinesisAppender<ILoggingEvent> kinesisAppender) {
        AwsCredentialsProvider credentialsProvider =
                StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        logProperties.getKinesis().getAccessKey(), logProperties.getKinesis().getAccessKey()));
        kinesisAppender.setCredentialsProvider(credentialsProvider);
    }

    private void setJsonLayout(KinesisAppender<ILoggingEvent> kinesisAppender) {
        KinesisJsonProvider provider = KinesisJsonProvider.builder()
                .appName(appName)
                .appPort(appPort)
                .environment(environment)
                .platform(logProperties.getPlatform())
                .version(version)
                .build();
        provider.start();

        JsonProviders<ILoggingEvent> providers = new JsonProviders<>();
        providers.addProvider(provider);
        providers.start();

        LoggingEventCompositeJsonLayout layout = new LoggingEventCompositeJsonLayout();
        layout.setProviders(providers);
        layout.start();

        kinesisAppender.setLayout(layout);
    }

    @Override
    public AbstractAppender getAbstractAppender() {
        return this;
    }

}
