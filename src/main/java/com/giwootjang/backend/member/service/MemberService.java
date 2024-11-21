package com.giwootjang.backend.member.service;

import com.giwootjang.backend.cache.Dummy;
import com.giwootjang.backend.cache.DummyRepository;
import com.giwootjang.backend.member.domain.Member;
import com.giwootjang.backend.member.domain.repository.MemberRepository;
import com.giwootjang.backend.member.domain.type.MemberLoginType;
import com.giwootjang.backend.member.domain.type.MemberStatus;
import com.giwootjang.backend.member.dto.request.MemberSignupRequest;
import com.giwootjang.backend.sms.dto.request.SmsAuthRequest;
import com.giwootjang.backend.sms.dto.request.SmsAuthVerificationRequest;
import com.giwootjang.backend.sms.service.SmsVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.UnifiedJedis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final SmsVerificationService smsVerificationService;
    private final MemberRepository memberRepository;
    private final DummyRepository dummyRepository;

    @Cacheable(cacheManager = "redisCacheManager", cacheNames = "users", value = "users", key = "#userId")
    public String getUser(String userId) {
        System.out.println("캐시에서 조회되지 않아 메서드가 실행됨! userId: " + userId);
        return userId;
    }

    public void cacheTest() {

        UnifiedJedis jedis = new UnifiedJedis();



        int a = 5;
    }

    public void dummyTest() {
        Dummy dummy = new Dummy(
                "Eng2015001", "John Doe", Dummy.Gender.MALE, 1);
        dummyRepository.save(dummy);

        Dummy retrievedDummy =
                dummyRepository.findById("Eng2015001").get();

        retrievedDummy.setName("Richard Watson");
        dummyRepository.save(dummy);

        dummyRepository.deleteById(dummy.getId());

        Dummy engDummy = new Dummy(
                "Eng2015001", "John Doe", Dummy.Gender.MALE, 1);
        Dummy medDummy = new Dummy(
                "Med2015001", "Gareth Houston", Dummy.Gender.MALE, 2);
        dummyRepository.save(engDummy);
        dummyRepository.save(medDummy);

        List<Dummy> dummys = new ArrayList<>();
        dummyRepository.findAll().forEach(dummys::add);
        int a = 5;
    }

    public boolean validateDuplicateId(final String userId) {
        return memberRepository.existsByMemberId(userId);
    }

    public boolean requestPhoneAuth(final SmsAuthRequest smsAuthRequest) {
        smsVerificationService.requestPhoneAuth(smsAuthRequest);
        return true;
    }

    public boolean verifyAuthNumber(final SmsAuthVerificationRequest smsAuthVerificationRequest) {
        return smsVerificationService.verifyAuthNumber(smsAuthVerificationRequest);
    }

    public boolean clearAuthNumber(final SmsAuthRequest smsAuthRequest) {
        smsVerificationService.clearAuthNumber(smsAuthRequest);
        return true;
    }

    private String encodePassword(final String password) {
        PasswordEncoder passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final String encodedPwd = passwordEncoder.encode(password);
        if (!passwordEncoder.matches(password, encodedPwd)) {
            throw new IllegalArgumentException("암호화된 비밀번호와 현재 비밀번호가 일치하지 않습니다.");
        }

        return encodedPwd;
    }

    public void processUserSignUp(final MemberSignupRequest userRequest) {
        final String userId = userRequest.getId();
        validateDuplicateId(userId);

        final String name = userRequest.getName();
        final String pwd = encodePassword(userRequest.getPassword());
        final String email = userRequest.getEmail();

        final String combinedPhone =
                smsVerificationService.combinePhoneNumberSegments(
                        userRequest.getFPhoneN(),
                        userRequest.getMPhoneN(),
                        userRequest.getBPhoneN());

        final String profileData = null;
        final BigDecimal amount = BigDecimal.ZERO;
        final BigDecimal points = BigDecimal.ZERO;
        final MemberStatus status = MemberStatus.ACTIVE;
        final MemberLoginType loginType = MemberLoginType.DEFAULT;

        final Member newMember = Member.of(userId, name, pwd, email, combinedPhone,
                profileData, status, loginType, amount, points);
        memberRepository.save(newMember);
    }
}
