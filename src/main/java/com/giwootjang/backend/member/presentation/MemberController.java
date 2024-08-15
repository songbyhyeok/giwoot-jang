package com.giwootjang.backend.member.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class MemberController {
    @GetMapping("/")
    public String showMain(Model model) {
        model.addAttribute("content", "domain/main :: content");
        return "index";
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("content", "domain/login :: content");
        return "index";
    }

    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("content", "domain/signup :: content");
        return "index";
    }

}
