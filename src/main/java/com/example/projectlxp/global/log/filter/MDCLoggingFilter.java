package com.example.projectlxp.global.log.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MDCLoggingFilter implements Filter {
    private static final String REQUEST_ID = "REQUEST_ID";

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String requestId = "request-" + createUUID();
        long startTime = System.currentTimeMillis();

        try {
            MDC.put(REQUEST_ID, requestId);

            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

            log.info("--> {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

            filterChain.doFilter(servletRequest, servletResponse);

            long duration = System.currentTimeMillis() - startTime;
            log.info(
                    "<-- {} {} {} ({}ms)",
                    httpResponse.getStatus(),
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    duration);
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }

    private String createUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
