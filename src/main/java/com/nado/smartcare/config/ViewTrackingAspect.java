package com.nado.smartcare.config;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ViewTrackingAspect {
    private static final Logger log = LoggerFactory.getLogger(ViewTrackingAspect.class);

    @Before("execution(* com.nado.smartcare.food.service.impl.FoodPlaceServiceImpl.incrementViews(..))")
    public void logBeforeIncrementViews(JoinPoint joinPoint) {
        String callerInfo = getCallerInfo();
        Long fno = (Long) joinPoint.getArgs()[0];

        log.info("incrementViews 호출 - fno: {}, 호출자: {}", fno, callerInfo);
    }

    private String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // stackTrace[0]는 getStackTrace 메서드 자체
        // stackTrace[1]는 현재 메서드 (getCallerInfo)
        // stackTrace[2]는 logBeforeIncrementViews
        // stackTrace[3]은 Spring AOP 프록시
        // stackTrace[4] 이상이 실제 호출 스택

        StringBuilder callerInfo = new StringBuilder();
        for (int i = 4; i < Math.min(stackTrace.length, 10); i++) { // 최대 6개 프레임 출력
            StackTraceElement frame = stackTrace[i];
            if (frame.getClassName().startsWith("com.nado.smartcare")) { // 프로젝트 패키지만 필터링
                callerInfo.append(frame.getClassName())
                        .append(".").append(frame.getMethodName())
                        .append("(").append(frame.getFileName())
                        .append(":").append(frame.getLineNumber())
                        .append(")\n");
            }
        }

        return callerInfo.toString();
    }
}
