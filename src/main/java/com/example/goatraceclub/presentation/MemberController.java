package com.example.goatraceclub.presentation;

import com.example.goatraceclub.application.interfaces.IKæledyrService;
import com.example.goatraceclub.application.interfaces.IMedlemService;
import com.example.goatraceclub.domain.Kæledyr;
import com.example.goatraceclub.domain.Medlem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final IMedlemService medlemService;
    private final IKæledyrService kæledyrService;

    @Autowired
    public MemberController(IMedlemService medlemService, IKæledyrService kæledyrService) {
        this.medlemService = medlemService;
        this.kæledyrService = kæledyrService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        Medlem currentMember = (Medlem) session.getAttribute("currentMember");
        if (currentMember == null) {
            return "redirect:/login";
        }

        // Hent antal kæledyr fra service laget
        List<Kæledyr> pets = kæledyrService.getKæledyrByMedlemId(currentMember.getId());

        model.addAttribute("memberName", currentMember.getName());
        model.addAttribute("memberEmail", currentMember.getEmail());
        model.addAttribute("memberAddress", currentMember.getAddresse());
        model.addAttribute("memberPhone", currentMember.getTelefon());
        model.addAttribute("memberSince", currentMember.getIndmeldelsesDato());
        model.addAttribute("petCount", pets.size());

        return "dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Medlem currentMember = (Medlem) session.getAttribute("currentMember");
        if (currentMember == null) {
            return "redirect:/login";
        }

        model.addAttribute("member", currentMember);
        return "profile";
    }

    @GetMapping("/pets")
    public String listPets(HttpSession session, Model model) {
        Medlem currentMember = (Medlem) session.getAttribute("currentMember");
        if (currentMember == null) {
            return "redirect:/login";
        }

        // Hent kæledyr fra service laget
        List<Kæledyr> pets = kæledyrService.getKæledyrByMedlemId(currentMember.getId());
        model.addAttribute("pets", pets);

        return "pets";
    }

    @GetMapping("/pets/register")
    public String showPetRegistrationForm(HttpSession session) {
        Medlem currentMember = (Medlem) session.getAttribute("currentMember");
        if (currentMember == null) {
            return "redirect:/login";
        }

        return "register-pet";
    }

    @PostMapping("/pets/register")
    public String registerPet(@RequestParam String goatName,
                              @RequestParam String race,
                              @RequestParam int weight,
                              @RequestParam String birthday,
                              HttpSession session,
                              Model model) {
        try {
            Medlem currentMember = (Medlem) session.getAttribute("currentMember");
            if (currentMember == null) {
                return "redirect:/login";
            }

            // Opret kæledyr via service laget
            kæledyrService.opretKæledyr(currentMember.getId(), goatName, race, weight, birthday);

            return "redirect:/member/pets?success=Ged oprettet!";
        } catch (Exception e) {
            model.addAttribute("error", "Der opstod en fejl ved oprettelse af geden: " + e.getMessage());
            return "register-pet";
        }
    }

    @GetMapping("/allgoats")
    public String showAllGoats(HttpSession session, Model model) {
        Medlem currentMember = (Medlem) session.getAttribute("currentMember");
        if (currentMember == null) {
            return "redirect:/login";
        }

        // Hent alle kæledyr fra service laget
        List<Kæledyr> allGoats = kæledyrService.getAlleKæledyr();
        model.addAttribute("goats", allGoats);

        return "all-goats";
    }

    @GetMapping("/membergoats")
    public String showMemberGoats(HttpSession session, Model model) {
        Medlem currentMember = (Medlem) session.getAttribute("currentMember");
        if (currentMember == null) {
            return "redirect:/login";
        }

        // Hent alle medlemmer med deres geder
        Map<String, List<Kæledyr>> medlemmerMedGeder = kæledyrService.getAlleMedlemmerMedGeder();
        model.addAttribute("medlemmerMedGeder", medlemmerMedGeder);

        return "member-goats";
    }
}