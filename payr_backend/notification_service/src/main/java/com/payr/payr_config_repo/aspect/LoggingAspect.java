package com.payr.payr_config_repo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    @Around("controllerLayer()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("Controller: {} called with args: {}",
                joinPoint.getSignature(),
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            log.error("Controller: {} threw exception: {}", joinPoint.getSignature(), e.getMessage());
            throw e;
        } finally {
            long timeTaken = System.currentTimeMillis() - start;
            log.info("Controller: {} completed in {} ms", joinPoint.getSignature(), timeTaken);
        }
    }

    @Around("serviceLayer()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Service: {} started", joinPoint.getSignature());
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            log.info("Service: {} finished", joinPoint.getSignature());
        }
    }
}
