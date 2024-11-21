package com.giwootjang.backend.common.config;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// TODO 1. META CSRF TOKEN ISSUE
// TODO 2. JSON 변환 ISSUE
// TODO 3. AOP PROXY ISSUE
// TODO 4. SPRING BOOT 3.2x parameter issue, cache no save

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                redisProperties.getHost(),
                redisProperties.getPort()
        );
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    private RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig() // default cache config 설정 적용
                .disableCachingNullValues() // null 값에 대한 캐싱 불허
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // key serialize 설정
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())); // value serialize 설정
                // .prefixCacheNameWith("Songbyhyeok:") // 전역 prefix 설정
                // .computePrefixWith(CacheKeyPrefix.prefixed("jforj::")) // 다음 코드로 전역 prefix 설정 대체 가능
                // .entryTtl(Duration.ofMinutes(1L));
    }

    private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("phones", redisCacheConfiguration().entryTtl(Duration.ofSeconds(60)));
        return redisCacheConfigurationMap;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager
                .builder()
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration())
                .withInitialCacheConfigurations(redisCacheConfigurationMap())
                .build();
    }
}
