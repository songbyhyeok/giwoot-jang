package com.giwootjang.backend.member.service;

import com.giwootjang.backend.aws.service.SimpleNotificationService;
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
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final SimpleNotificationService simpleNotificationService;
    private static final int PHONE_LENGTH = 13;

    public boolean validateDuplicateId(final String userId) {
        return memberRepository.existsByMemberId(userId);
    }

    public String processSmsVerificationCode(final String phoneNumber) {
        /*
        1. 클라이언트가 입력한 번호를 서버에 인증 요청(클라이언트)
            -> hidden 상태에서 show 상태로 처리 [v]
            -> 비동기로 휴대폰 번호 보내기 [v]
        */
        // 2. 서버는 인증번호 생성 및 응답 처리
        //  -> 인증코드 4자리 생성 [v]
        final String authNumber = Integer.toString(new Random().nextInt(10000));

        //    -> 주제 확인
        final String topicArn = simpleNotificationService.getSNSTopicArn();

        //      SNS 주제가 있는지 확인한다.
        //      없을 경우 SNS 주제를 생성한다.

        //    -> 구독 확인(번호 확인)
        if (simpleNotificationService.isSubscribedPhoneNumber(phoneNumber)) {
            //    -> 사용자에게 인증코드 보내기
            final boolean isNotificationSent =
                    simpleNotificationService.verifySMSMessagePublished(phoneNumber, authNumber);
            if (isNotificationSent) {
                return authNumber;
            } else {
                throw new RuntimeException("인증 코드 발송에 실패했습니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 전화번호는 구독되지 않았습니다.");
        }

        //       -> 샌드박스가 꽉찼는지 확인.
        //       -> 꽉찼다면 임의 하나 삭제
        //       -> 꽉차있지 않다면 번호로 구독 생성
        //simpleNotificationService.subscribeSMS(topicArn, phoneNumber);
        //simpleNotificationService.publishSmsMessage(topicArn, authNumber);
        //    -> 휴대폰 번호를 바로 구독 등록

        /* 3. 사용자는 인증번호 입력 및 확인
            -> 휴대폰의 인증코드와 응답의 인증코드와 일치 여부 확인
            -> 맞을 경우 가입 승인 처리 가능하게(bool true 허용)
            -> 시간제한, 횟수 제한(초과 시 인증요청 다시 유도, hidden 처리)
            -> cloudwatch, 카톡 알림에 json 형태로 데이터 뿌리기
        */

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

    private String combinePhoneNumberSegments(final String fPhoneN, final String mPhoneN, final String bPhoneN) {
        final String phoneNumber = fPhoneN + '-' + mPhoneN + '-' + bPhoneN;
        if (phoneNumber.length() != PHONE_LENGTH) {
            throw new IllegalArgumentException("휴대폰 번호 길이는 13자리입니다. 현재 길이: " + phoneNumber.length());
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
                combinePhoneNumberSegments(userRequest.getFPhoneN(), userRequest.getMPhoneN(), userRequest.getBPhoneN());

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
