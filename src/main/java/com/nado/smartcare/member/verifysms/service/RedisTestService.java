package com.nado.smartcare.member.verifysms.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RedisTestService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void testRedisConnection() {
        redisTemplate.opsForValue().set("springTestKey", "springTestValue");
        String value = redisTemplate.opsForValue().get("springTestKey");
        System.out.println("Redis에서 가져온 값: " + value);
    }
}
