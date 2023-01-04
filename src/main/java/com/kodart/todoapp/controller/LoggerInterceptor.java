package com.kodart.todoapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerInterceptor sam w sobie, nawet oznaczony jako komponent nie będzie działał.
 * Wymaga do tego klasy konfigurującej, IMPLEMENTUJĄCEJ interfejs WebMvcConfigurer.
 * Tam zostaje przeciążona metoda addInterceptors, która wykorzystuje rejestr wszystkich klas,
 * które implementują interfejs HandlerInterceptor.
 * Jako pierwsze jedank zawsze honorowane są metody należące do JavaEE/Javax, tj. klasa LoggerFilter.
 */
@Component
public class LoggerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        logger.info("[preHandle] " + request.getMethod() + " " + request.getRequestURI());
        return true;
    }
}
