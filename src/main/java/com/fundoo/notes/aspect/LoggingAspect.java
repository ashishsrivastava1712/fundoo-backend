
package com.fundoo.notes.aspect;

/*
 * Class Name  : LoggingAspect
 * Description : Provides centralized logging for service method execution.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.fundoo.notes.service..*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {

        logger.info(
                "Executing service method: {}",
                joinPoint.getSignature().toShortString()
        );
    }

    @After("execution(* com.fundoo.notes.service..*(..))")
    public void logAfterMethod(JoinPoint joinPoint) {

        logger.info(
                "Completed service method: {}",
                joinPoint.getSignature().toShortString()
        );
    }
}
