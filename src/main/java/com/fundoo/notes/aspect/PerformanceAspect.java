
package com.fundoo.notes.aspect;

/*
 * Class Name  : PerformanceAspect
 * Description : Measures execution time of service methods.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("execution(* com.fundoo.notes.service..*(..))")
    public Object measureExecutionTime(
            ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime =
                    System.currentTimeMillis() - startTime;

            logger.info(
                    "Method: {} | Execution Time: {} ms",
                    joinPoint.getSignature().toShortString(),
                    executionTime
            );
        }
    }
}
