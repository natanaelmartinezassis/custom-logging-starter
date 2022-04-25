package com.example.appname.log.properties;

import ch.qos.logback.classic.Level;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "log", ignoreUnknownFields = false)
public class LogProperties implements Serializable {

    private String platform = "";
    private String defaultPackage = "";
    private String level = Level.INFO.levelStr;
    private List<PackageInfo> extraPackages = new ArrayList<>();
    private Logstash logstash = new Logstash();
    private Kinesis kinesis = new Kinesis();

    @Getter
    @Setter
    public static class PackageInfo implements Serializable {
        private String name = "";
        private String level = Level.INFO.levelStr;
    }

    @Getter
    @Setter
    public static class Logstash implements Serializable {
        private boolean enabled = false;
        private String host = "localhost";
        private int port = 5000;
        private int queueSize = 512;
    }

    @Getter
    @Setter
    public static class Kinesis implements Serializable {
        private boolean enabled = false;
        private String endpoint = "kinesis.us-east-1.amazonaws.com";
        private String region = "us-east-1";
        private String accessKey = "";
        private String secretKey = "";
        private String streamName;
        private int bufferSize = 2000;
        private int threadCount = 20;
        private int maxRetries = 3;
        private int shutdownTimeout = 30;
        private String encoding = "UTF-8";
    }

}
