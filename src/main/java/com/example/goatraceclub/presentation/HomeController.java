package com.example.goatraceclub.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("loggedIn") != null && (Boolean) session.getAttribute("loggedIn")) {
            return "redirect:/member/dashboard";
        }
        return "index";
    }
}