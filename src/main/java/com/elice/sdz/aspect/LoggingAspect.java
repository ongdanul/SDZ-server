package com.elice.sdz.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.elice.sdz.*.controller.*Controller.*(..))")
    public void pointcut() {}

    @Before("pointcut()")
    public void request(JoinPoint joinPoint) throws JsonProcessingException {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String requestToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(args);

        log.info("Request ({}): \n{}", methodName, requestToJson);
    }

    @AfterReturning(value = "pointcut()", returning = "result")
    public void response(JoinPoint joinPoint, Object result) throws JsonProcessingException {
        String methodName = joinPoint.getSignature().getName();

        String responseToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);

        log.info("Response ({}): \n{}", methodName, responseToJson);
    }

    @AfterThrowing(value = "pointcut()", throwing = "exception")
    public void exception(JoinPoint joinPoint, Exception exception) throws JsonProcessingException {
        String methodName = joinPoint.getSignature().getName();

        String exceptionToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exception);

        log.error("Exception ({}): \n{}", methodName, exceptionToJson);
    }
}
