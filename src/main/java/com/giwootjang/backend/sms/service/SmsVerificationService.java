package com.giwootjang.backend.sms.service;

import com.giwootjang.backend.aws.service.SimpleNotificationService;
import com.giwootjang.backend.common.config.RedisConfig;
import com.giwootjang.backend.sms.dao.SmsDao;
import com.giwootjang.backend.sms.dto.request.SmsAuthRequest;
import com.giwootjang.backend.sms.dto.request.SmsAuthVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsVerificationService {
    private final RedisConfig redisConfig;
    private final SimpleNotificationService simpleNotificationService;
    private final SmsDao smsDao;

    private static final int PHONE_LENGTH = 13;

    public String combinePhoneNumberSegments(final String fPhoneN, final String mPhoneN, final String bPhoneN) {
        final String phoneNumber = fPhoneN + '-' + mPhoneN + '-' + bPhoneN;
        if (phoneNumber.length() != PHONE_LENGTH) {
            throw new IllegalArgumentException("휴대폰 번호 길이는 13자리입니다. 현재 길이: " + phoneNumber.length());
        }

        return phoneNumber;
    }

    public boolean requestPhoneAuth(final SmsAuthRequest smsAuthRequest) {
        /*
        1. 클라이언트가 입력한 번호를 서버에 인증 요청(클라이언트)
            -> hidden 상태에서 show 상태로 처리 [v]
            -> 비동기로 휴대폰 번호 보내기 [v]
        */

        final String combinedPhone =
                combinePhoneNumberSegments(smsAuthRequest.getFPhoneN(),
                        smsAuthRequest.getMPhoneN(),
                        smsAuthRequest.getBPhoneN());

//      TODO 2. 인증번호 생성 및 cache 에 저장
        final String authCode = smsDao.getPhoneAuthCode(smsAuthRequest.getName(), combinedPhone);
        System.out.println("생성된 인증번호: " + authCode);

        RedisConnectionFactory redisConnectionFactory = redisConfig.redisConnectionFactory();
        CacheManager redisCacheManager = redisConfig.redisCacheManager(redisConnectionFactory);
        Cache cache = redisCacheManager.getCache(smsAuthRequest.getName() + ':' + combinedPhone);
        if (cache == null) {
            throw new IllegalArgumentException("Cache not found");

        }

        //    -> 주제 확인
//        final String topicArn = simpleNotificationService.getSNSTopicArn();
//
//        //      SNS 주제가 있는지 확인한다.
//        //      없을 경우 SNS 주제를 생성한다.
//
//        //    -> 구독 확인(번호 확인)
//        if (simpleNotificationService.isSubscribedPhoneNumber(phoneNumber)) {
//            //    -> 사용자에게 인증코드 보내기
//            final boolean isNotificationSent =
//                    simpleNotificationService.verifySMSMessagePublished(phoneNumber, authNumber);
//
//            if (isNotificationSent) {
        return false;

//
//
//
//            } else {
//                throw new RuntimeException("인증 코드 발송에 실패했습니다.");
//            }
//        } else {
//            throw new IllegalArgumentException("해당 전화번호는 구독되지 않았습니다.");
//        }

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

    public boolean verifyAuthNumber(final SmsAuthVerificationRequest smsAuthVerificationRequest) {
        /* 3. 사용자는 인증번호 입력 및 확인
            -> 휴대폰의 인증코드와 응답의 인증코드와 일치 여부 확인
            -> 맞을 경우 가입 승인 처리 가능하게(bool true 허용)
            -> 시간제한, 횟수 제한(초과 시 인증요청 다시 유도, hidden 처리)
            -> cloudwatch, 카톡 알림에 json 형태로 데이터 뿌리기
        */

        final String combinedPhone =
                combinePhoneNumberSegments(smsAuthVerificationRequest.getFPhoneN(),
                        smsAuthVerificationRequest.getMPhoneN(),
                        smsAuthVerificationRequest.getBPhoneN());

        final String authCode = smsDao.getPhoneAuthCode(smsAuthVerificationRequest.getName(), combinedPhone);

        boolean isAuthConfirmed = false;
        if (authCode.equals(smsAuthVerificationRequest.getAuthNumber())) {
            System.out.println("인증번호 일치");
            isAuthConfirmed = true;
        } else {
            System.out.println("인증번호 불일치");
        }

        return isAuthConfirmed;
    }

    public boolean clearAuthNumber(final SmsAuthRequest smsAuthRequest) {
        final String combinedPhone =
                combinePhoneNumberSegments(smsAuthRequest.getFPhoneN(),
                        smsAuthRequest.getMPhoneN(),
                        smsAuthRequest.getBPhoneN());

        smsDao.clearPhoneAuthCode(smsAuthRequest.getName(), combinedPhone);

        return true;
    }
}
