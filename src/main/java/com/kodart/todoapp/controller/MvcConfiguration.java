package com.kodart.todoapp.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

/**
 * Klasa implementująca interfejs WebMvcConfigurer po pierwsze umożliwie działanie
 * Springowych Interceptorów, dodatkowo poniższa implementacja dodatkowo automatyzuje ich dodawanie
 * do kontekstu Springa na podstawie implementacji interfejsu HandlerInterceptor.
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private final Set<HandlerInterceptor> handlerInterceptors;

    public MvcConfiguration(final Set<HandlerInterceptor> handlerInterceptors) {
        this.handlerInterceptors = handlerInterceptors;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        handlerInterceptors.forEach(registry::addInterceptor);
    }
}
