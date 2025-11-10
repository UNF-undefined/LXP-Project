package com.example.projectlxp.global.log.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;

import com.example.projectlxp.global.log.filter.MDCLoggingFilter;

@Configuration
@EnableAspectJAutoProxy
public class LogConfig {

    @Bean
    public FilterRegistrationBean<MDCLoggingFilter> mdcLoggingFilterRegistration(
            MDCLoggingFilter filter) {
        FilterRegistrationBean<MDCLoggingFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");

        return registration;
    }
}
