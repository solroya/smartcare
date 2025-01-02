package com.nado.smartcare.member.verifysms.controller;

import com.nado.smartcare.member.verifysms.service.RedisTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTestController {

    private final RedisTestService redisTestService;

    public RedisTestController(RedisTestService redisTestService) {
        this.redisTestService = redisTestService;
    }

    @GetMapping("/redis/test")
    public String testRedis() {
        redisTestService.testRedisConnection();
        return "Redis 연결 테스트 완료. 로그를 확인하세요.";
    }
}
