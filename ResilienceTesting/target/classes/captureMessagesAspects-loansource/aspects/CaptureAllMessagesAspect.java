package io.pivotal.loancheck.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component("around")
@Aspect
public class CaptureAllMessagesAspect {

	 private int counter = 1;

}