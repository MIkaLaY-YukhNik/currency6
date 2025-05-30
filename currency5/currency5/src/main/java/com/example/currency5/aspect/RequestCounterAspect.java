package com.example.currency5.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.currency5.service.RequestCounter;

@Aspect
@Component
public class RequestCounterAspect {
    private final RequestCounter requestCounter;

    public RequestCounterAspect(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }


    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void countAllRequests() {
        requestCounter.incrementTotal();
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void countSuccessfulRequests(Object result) {
        requestCounter.incrementSuccessful();
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void countFailedRequests(Exception ex) {
        requestCounter.incrementFailed();
    }
}
