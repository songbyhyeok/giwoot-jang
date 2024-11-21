package com.giwootjang.backend.member.presentation;

import com.giwootjang.backend.member.dto.request.MemberSignupRequest;
import com.giwootjang.backend.member.service.MemberService;
import com.giwootjang.backend.sms.dto.request.SmsAuthRequest;
import com.giwootjang.backend.sms.dto.request.SmsAuthVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService     memberService;

    @GetMapping("/")
    public String showMain(Model model) {

//        memberService.dummyTest();
        memberService.cacheTest();

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
    public String submit(Model model, @ModelAttribute MemberSignupRequest signupRequest) {
        System.out.println(signupRequest);

        memberService.processUserSignUp(signupRequest);
        model.addAttribute(signupRequest);
        model.addAttribute("content", "domain/login :: content");

        return "index";
    }

    @GetMapping("/signup/verification/ids/{id}")
    public ResponseEntity<Boolean> checkDuplicateId(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(memberService.validateDuplicateId(id));
    }

    @PostMapping("/signup/verification/phones/auth")
    public ResponseEntity<Boolean> generatePhoneAuthCode(@RequestBody SmsAuthRequest smsAuthRequest) {
        System.out.println(smsAuthRequest.toString());
        return ResponseEntity.ok(memberService.requestPhoneAuth(smsAuthRequest));
    }

    @PostMapping("/signup/verification/phones/confirm")
    public ResponseEntity<Boolean> checkPhoneAuthCode(@RequestBody SmsAuthVerificationRequest smsAuthVerificationRequest) {
        System.out.println(smsAuthVerificationRequest);
        return ResponseEntity.ok(memberService.verifyAuthNumber(smsAuthVerificationRequest));
    }

    @PostMapping("/signup/verification/phones/clear")
    public ResponseEntity<Boolean> clearPhoneAuthCode(@RequestBody SmsAuthRequest smsAuthRequest) {
        System.out.println(smsAuthRequest);
        return ResponseEntity.ok(memberService.clearAuthNumber(smsAuthRequest));
    }
}
