package com.giwootjang.backend.sms.dao;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SmsDao {
    @Cacheable(value = "phones", key = "#name + ':' + #phoneN")
    public String getPhoneAuthCode(final String name, final String phoneN) {
        final String authCode = String.format("%06d", new Random().nextInt(1000000));
        System.out.println("캐시에서 조회되지 않아 인증코드가 생성됨!");

        return authCode;
    }

    @CacheEvict(value = "phones", key = "#name + ':' + #phoneN")
    public void clearPhoneAuthCode(final String name, final String phoneN) {
        System.out.println("캐시 삭제됨");
    }
}
