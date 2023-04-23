package io.pivotal.loancheck.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CaptureSomeMessagesAspect {
    private int counter = 1;

//	@Pointcut(value = "execution(* io.pivotal.loancheck.LoanChecker.checkAndSortLoans(..))")
//	private void captureapproved() {}

	@Pointcut("@annotation(org.springframework.cloud.stream.annotation.StreamListener)")
	public void findStreamListeners() {}

	 @Around(value = "findStreamListeners()")
	 public Object catchMessageForcaptureapprove(ProceedingJoinPoint pjp) throws Throwable {
		 counter++;
		 if (counter % 3 != 0) {
			 System.out.println("MESSAGE DISCARDED");
			 throw new NullPointerException("The message is discarded");
		 } else  {
			 return pjp.proceed();
		 }
	 }
}