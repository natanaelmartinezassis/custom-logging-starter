# Custom Logging Starter  
![image](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  
_Auto-configurable starter for recording TRACED log outputs using **[Slf4j](https://www.slf4j.org)** + **[Logback](https://logback.qos.ch)** for Spring Boot Applications._

---

# Appenders

Three appenders are available:

### Default

* __Console Appender__: responsible for printing the log records in ```System.out``` or ```System.err```.

### Configurable

* __Logstash Appender__ (JSON): responsible for asynchronously sending the log records from packages described
  in ```log.defaultPackage``` and ```log.extraPackages``` to **_LOGSTASH_** using an appender of
  type ```net.logstash.logback.appender.LogstashTcpSocketAppender```.


* __AWS Kinesis Appender__ (JSON): responsible for asynchronously sending the log records from packages described
  in ```log.defaultPackage``` and ```log.extraPackages``` to **_AWS KINESIS_** using the
  library [kinesis-logback-appender](https://github.com/hyp3rventures/kinesis-logback-appender).

---

# Instrumentation (Distributed Trace)  

In order to track requests between different microservices,
the [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth) library was used, which provides trace and
span id's automatically injecting them into the Slf4j MDC
(_Mapped Diagnostic Context_) and propagating to all log appenders.
  
---

# Metadata

[Configurable Appenders](#Configurable) use some application metadata to enrich the information sent to their
destinations:

```
date_time: <log record date and time in the format YYYY-mm-ddTHH:mm:ss.Z>
app_name: ${spring.application.name:_undefined}
app_port: ${server.port:8080}
environment: ${spring.profiles.active:_undefined}
level: <log level>
logger_name: <class of the log record>
platform: ${log.platform}
version: ${info.app.version:0}
traceId: <automatically generated traceId>
spanId: <automatically generated spanId>
```

---

# WEB Applications

For the case of Web Applications - that is, applications tha use the ```Spring WebApplicationContext``` - a log record
containing the REQUEST and RESPONSE data will be automatically generated for every request:

```
>>> REQUEST
{
    "date_time":"2022-02-08T15:45:48.052",
    "app_name":"your_app_name",
    "app_port":"8080",
    "environment":"dev",
    "level":"INFO",
    "logger_name":"com.example.appname.log.web.service.LogHttpRequest",
    "message":">> REQUEST DATA: method=[POST], uri=[/v1/resource/add], headers=[{Connection=Keep-Alive, User-Agent=Apache-HttpClient/4.5.13 (Java/11.0.13), Host=localhost:8080, Accept-Encoding=gzip,deflate, Content-Length=107, Content-Type=application/json}], ",
    "platform":"XPTO",
    "version":"1.0",
    "method":"POST",
    "uri":"/v1/resource/add",
    "request_params":"{}",
    "user_agent":"Apache-HttpClient/4.5.13 (Java/11.0.13)",
    "traceId":"19682beaa6b3e2c0",
    "spanId":"19682beaa6b3e2c0"
}
```

```
<<< RESPONSE
{
    "date_time":"2022-02-08T15:45:48.626",
    "app_name":"your_app_name",
    "app_port":"8080",
    "environment":"dev",
    "level":"INFO",
    "logger_name":"com.example.appname.log.web.service.LogHttpResponse",
    "message":"<< RESPONSE DATA: method=[POST], uri=[/v1/resource/add], statusCode=[200], elapsedTime=[577 millis], ",
    "platform":"XPTO",
    "version":"1.0",
    "payload":"",
    "method":"POST",
    "uri":"/v1/resource/add",
    "user_agent":"Apache-HttpClient/4.5.13 (Java/11.0.13)",
    "status_code":"200",
    "exec_start":"2022-02-08T15:45:48.048878",
    "exec_end":"2022-02-08T15:45:48.626178",
    "elapsed_time_in_millis":"577",
    "traceId":"19682beaa6b3e2c0",
    "spanId":"19682beaa6b3e2c0"
}
```

---

# How To

## First Step: Configuration

1. Import the starter using your library manager.  
   For example, using Maven, add the following snippet to your pom.xml file:

```
<!-- Custom Logging Starter -->
<dependency>
    <groupId>com.example.appname</groupId>
    <artifactId>custom-logging-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

2. Configure the usage parameters in your application's __application.yml__ file using Spring Boot.  
   Remember, _Console Appender_ is default and doesn't need to be configured. _Logstash_ and _Kinesis_ Appenders can be
   enabled or disabled using the ```log.logstash.enabled``` and ```log.kinesis.enabled``` flags.  
   These Appenders are disabled by default. That is, not configuring them in the application.yml file is also an option
   in case you don't use them:

```
log:
  platform: ${PLATFORM:XPTO}
  defaultPackage: com.example.appname
  level: DEBUG
  extraPackages:
    - name: org.springframework.cloud
      level: WARN
  logstash:
    enabled: true
    host: localhost
    port: 5010
    queueSize: 512
  kinesis:
    enabled: true
    endpoint: "http://localhost:4566"
    region: us-east-1 # used only if the endpoint field is not filled in
    access-key: "123"
    secret-key: "abc"
    streamName: log-stream
    bufferSize: 2000
    threadCount: 20
    maxRetries: 3
    shutdownTimeout: 30
    encoding: UTF-8
```

## Second Step: Using

The Logstash library allows using a structured logging mechanism that is responsible for indexing and interpolating the
log message. That way, every key/value pair will be automatically indexed in the log output.  
For example:

```
package com.example.appname

import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
public class Example {

    private void log() {
        Foo foo = new Foo("barValue");
        
        log.info("Foo object with bar value {}", 
          kv("bar", foo.getBar())
        );
    }

}

```

The output of this log record will look something like this:

```
{
    "date_time":"2022-02-08T15:47:01.053",
    "app_name":"your_app_name",
    "app_port":"8080",
    "environment":"dev",
    "level":"INFO",
    "logger_name":"com.example.appname.Example",
    "message":"Foo object with bar value bar=barValue", # <-- interpolated message
    "bar": "barValue", # <-- auto-indexed key
    "platform":"XPTO",
    "version":"1.0",
    "traceId":"19682beaa6b3e2c0",
    "spanId":"19682beaa6b3e2c0"
}
```

---

# ATTENTION

> This is an example project. All packages in this project are inside the ```com.example.appname``` structure. To take
> full advantage of this project, fork this project by changing the current package structure to your package
> structure.  
> Preferably use an IDE.


