package com.giwootjang.backend.member.presentation;

//import com.giwootjang.backend.cache.Dummy;
//import com.giwootjang.backend.cache.DummyRepository;
import com.giwootjang.backend.cache.SquaredCalculator;
import com.giwootjang.backend.member.dto.request.MemberSignupRequest;
import com.giwootjang.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final SquaredCalculator squaredCalculator;
//    private final DummyRepository dummyRepository;

    @GetMapping("/")
    public String showMain(Model model) {

        //squaredCalculator.whenCalculatingSquareValueAgain_thenCacheHasAllValues();
//        Dummy dummy = new Dummy(
//                "Eng2015001", "John Doe", Dummy.Gender.MALE, 1);
//        dummyRepository.save(dummy);
//
//        Dummy retrievedDummy =
//                dummyRepository.findById("Eng2015001").get();
//
//        retrievedDummy.setName("Richard Watson");
//        dummyRepository.save(dummy);
//
//        dummyRepository.deleteById(dummy.getId());
//
//        Dummy engStudent = new Dummy(
//                "Eng2015001", "John Doe", Dummy.Gender.MALE, 1);
//        Dummy medStudent = new Dummy(
//                "Med2015001", "Gareth Houston", Dummy.Gender.MALE, 2);
//        dummyRepository.save(engStudent);
//        dummyRepository.save(medStudent);
//
//        List<Dummy> students = new ArrayList<>();
//        dummyRepository.findAll().forEach(students::add);

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

    @GetMapping("/signup/verification/ids/{id}")
    public ResponseEntity<Boolean> checkDuplicateId(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(memberService.validateDuplicateId(id));
    }

    @GetMapping("/signup/verification/phones/{phone}")
    public ResponseEntity<String> checkDuplicateCode(@PathVariable(name = "phone") String phone) {
        return ResponseEntity.ok(memberService.processSmsVerificationCode(phone));
    }

}
