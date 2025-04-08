package com.example.goatraceclub.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("memberName", "Test Medlem");
        return "dashboard";
    }

    @GetMapping("/profile")
    public String showProfile() {
        return "dashboard";
    }

    @GetMapping("/pets")
    public String listPets() {
        return "pets";
    }

    @GetMapping("/pets/register")
    public String showPetRegistrationForm() {
        return "register-pet";
    }
}