package cluz.com.agenda.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectLog {

    @Around("@annotation(cluz.com.agenda.config.annotations.Log)")
    public Object automaticLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var signature = proceedingJoinPoint.getSignature();
        log.info("Starting {}", signature);
        Object result = proceedingJoinPoint.proceed();
        log.info("Finished {}", signature);
        return result;
    }
}
