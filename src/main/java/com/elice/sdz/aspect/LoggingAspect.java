package com.elice.sdz.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.elice.sdz..*(..))")
    public void pointcut() {}

    @Before("pointcut()")
    public void request(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Request: {} - Args: {}", methodName, args);
    }

    @AfterReturning(value = "pointcut()", returning = "result")
    public void response(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Response: {} - Result: {}", methodName, result);
    }

    @AfterThrowing(value = "pointcut()", throwing = "exception")
    public void exception(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        log.error("Exception in {} - Message: {}", methodName, exception.getMessage(), exception);
    }
}
