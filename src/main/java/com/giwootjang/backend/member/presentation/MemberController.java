package com.giwootjang.backend.member.presentation;

import com.giwootjang.backend.member.dto.request.MemberSignupRequest;
import com.giwootjang.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

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

    @PostMapping("/signup")
    public String submit(Model model, @ModelAttribute MemberSignupRequest userRequest) {
        memberService.processUserSignUp(userRequest);

        model.addAttribute(userRequest);
        model.addAttribute("content", "domain/login :: content");

        return "index";
    }

    @GetMapping("/user")
    public ResponseEntity<Boolean> checkDuplicateId(@RequestParam(name = "id") String id) {
        return ResponseEntity.ok(memberService.validateDuplicateId(id));
    }

}
