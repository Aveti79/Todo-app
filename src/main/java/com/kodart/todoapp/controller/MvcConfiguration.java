package com.kodart.todoapp.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private final Set<HandlerInterceptor> handlerInterceptors;

    public MvcConfiguration(final Set<HandlerInterceptor> handlerInterceptors) {
        this.handlerInterceptors = handlerInterceptors;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        //Tym sposobem dodajemy pojedynczy interceptor, żeby to zautomatyzować trzeba skorzystać ze sposobu niżej
        //dodając wcześniej kolekcję interceptorów i parametr w konstruktorze
        //registry.addInterceptor(new LoggerInterceptor());
        handlerInterceptors.forEach(registry::addInterceptor);
    }
}
