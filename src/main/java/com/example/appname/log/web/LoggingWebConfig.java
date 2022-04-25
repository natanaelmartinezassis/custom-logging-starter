package com.example.appname.log.web;

import com.example.appname.log.web.interceptor.HttpRequestLogging;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("com.example.appname")
@ConditionalOnWebApplication
public class LoggingWebConfig implements WebMvcConfigurer {

    final HttpRequestLogging loggingInterceptor;

    public LoggingWebConfig(final HttpRequestLogging loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    @ConditionalOnMissingClass
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }

}
