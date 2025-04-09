package com.example.goatraceclub.presentation;

import com.example.goatraceclub.application.interfaces.IMedlemService;
import com.example.goatraceclub.domain.Medlem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    private final IMedlemService medlemService;

    @Autowired
    public LoginController(IMedlemService medlemService) {
        this.medlemService = medlemService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               Model model,
                               HttpSession session) {
        try {
            Optional<Medlem> optionalMedlem = medlemService.login(email, password);

            if (optionalMedlem.isPresent()) {
                Medlem medlem = optionalMedlem.get();
                session.setAttribute("currentMember", medlem);
                session.setAttribute("loggedIn", true);
                return "redirect:/member/dashboard";
            } else {
                model.addAttribute("error", "Ugyldig email eller kodeord");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Der opstod en fejl: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String address,
                                      @RequestParam String telephone,
                                      Model model) {
        try {
            medlemService.opretMedlem(name, email, password, address, telephone);
            return "redirect:/login?success=Registration successful";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Der opstod en fejl: " + e.getMessage());
            return "register";
        }
    }
}