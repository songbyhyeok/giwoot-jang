package com.giwootjang.backend.member.service;

import com.giwootjang.backend.member.domain.Member;
import com.giwootjang.backend.member.domain.repository.MemberRepository;
import com.giwootjang.backend.member.domain.type.MemberLoginType;
import com.giwootjang.backend.member.domain.type.MemberStatus;
import com.giwootjang.backend.member.dto.request.MemberSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private static final int PHONE_LENGTH = 13;

    public boolean validateDuplicateId(final String userId) {
        boolean isDuplicated = memberRepository.existsByMemberId(userId);
        return isDuplicated;
    }

    private String encodePassword(final String password) {
        PasswordEncoder passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();

        final String result = passwordEncoder.encode(password);
        if (!passwordEncoder.matches(password, result)) {
            throw new IllegalArgumentException("암호화된 비밀번호와 현재 비밀번호가 일치하지 않습니다.");
        }
        return result;
    }

    private String combinePhoneNumberSegments(final String fPhoneN, final String mPhoneN, final String bPhoneN) {
        final String phoneNumber = fPhoneN + '-' + mPhoneN + '-' + bPhoneN;
        if (phoneNumber.length() != PHONE_LENGTH) {
            throw new IllegalArgumentException("휴대폰 번호 길이는 13자리입니다. " + phoneNumber.length());
        }

        return phoneNumber;
    }

    public void processUserSignUp(final MemberSignupRequest userRequest) {
        final String userId = userRequest.getId();
        validateDuplicateId(userId);

        final String name = userRequest.getName();
        final String pwd = encodePassword(userRequest.getPassword());
        final String email = userRequest.getEmail();
        final String phone =
                combinePhoneNumberSegments(userRequest.getFPhone(), userRequest.getMPhone(), userRequest.getBPhone());
        final String profileData = null;
        final BigDecimal amount = BigDecimal.ZERO;
        final BigDecimal points = BigDecimal.ZERO;
        final MemberStatus status = MemberStatus.ACTIVE;
        final MemberLoginType loginType = MemberLoginType.DEFAULT;

        final Member newMember = Member.of(userId, name, pwd, email, phone,
                profileData, status, loginType, amount, points);
        memberRepository.save(newMember);
    }
}
