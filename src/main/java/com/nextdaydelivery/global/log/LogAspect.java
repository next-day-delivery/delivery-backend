package com.nextdaydelivery.global.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.nextdaydelivery..*Controller.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* com.nextdaydelivery..*Service.*(..))")
    public void service() {
    }

    @Around("controller() || service()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String layer = getLayer(signature);
        String className = className(signature);
        String methodName = methodName(signature);

        if (log.isDebugEnabled()) {
            log.debug("[{}] START: {}.{} | Args: {}", layer, className, methodName, formatArgs(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.info("[{}] {}.{} | SUCCESS | {}ms", layer, className, methodName, duration);

            if (log.isDebugEnabled()) {
                log.debug("[{}] END: {}.{} | Ret: {}", layer, className, methodName, formatResult(result));
            }

            return result;

        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("[{}] {}.{} | FAIL ({}) | {}ms",
                    layer,
                    className,
                    methodName,
                    t.getClass().getSimpleName(),
                    duration, t);
            throw t;
        }
    }

    private String getLayer(MethodSignature signature) {
        String className = signature.getDeclaringType().getSimpleName();
        if (className.endsWith("Controller")) {
            return "API";
        }
        if (className.endsWith("Service")) {
            return "SVC";
        }
        return "ETC";
    }

    private String className(MethodSignature signature) {
        return signature.getDeclaringType().getSimpleName();
    }

    private String methodName(MethodSignature signature) {
        return signature.getName();
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return args.length + " args passed";
    }

    private String formatResult(Object result) {
        if (result == null) {
            return "void";
        }
        return result.getClass().getSimpleName();
    }
}
