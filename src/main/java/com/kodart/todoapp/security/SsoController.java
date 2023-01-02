package com.kodart.todoapp.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class SsoController {

    @GetMapping("/logout")
    String logout (HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/login")
    String login() {
        return "index";
    }

}
