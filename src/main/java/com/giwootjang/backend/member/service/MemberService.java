package com.giwootjang.backend.member.service;

import com.giwootjang.backend.member.domain.Member;
import com.giwootjang.backend.member.domain.repository.MemberRepository;
import com.giwootjang.backend.member.domain.type.MemberLoginType;
import com.giwootjang.backend.member.domain.type.MemberStatus;
import com.giwootjang.backend.member.dto.request.MemberSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    private boolean encodePassword() {

        return true;
    }

    private String combinePhoneNumberSegments(final String fPhoneN, final String mPhoneN, final String bPhoneN) {
        final String phoneNumber = fPhoneN + '-' + mPhoneN + '-' + bPhoneN;
        System.out.println("phoneNumber = " + phoneNumber);

        return phoneNumber;
    }
    // 휴대폰 인증


    public boolean processUserSignUp(final MemberSignupRequest userRequest) {
        final String userId = userRequest.getId();
        final String name = userRequest.getName();
        final String pwd = userRequest.getPassword();
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
        return true;
    }
}
