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

	 @Pointcut(value = "execution(public abstract org.springframework.messaging.SubscribableChannel io.pivotal.loancheck.LoanProcessor.sourceOfLoanApplications())")
	 private void capturesourceOfLoanApplications() {}

	 @Pointcut(value = "execution(public abstract org.springframework.messaging.MessageChannel io.pivotal.loancheck.LoanProcessor.declined())")
	 private void capturedeclined() {}

	 @Pointcut(value = "execution(public abstract org.springframework.messaging.MessageChannel io.pivotal.loancheck.LoanProcessor.approved())")
	 private void captureapproved() {}

	 @Around(value = "capturesourceOfLoanApplications()")
	 public void catchMessageForcapturesourceOfLoanApplication(ProceedingJoinPoint pjp) throws Throwable {
	 	 System.out.println("ABORTING ALL");
	 }

	 @Around(value = "capturedeclined()")
	 public void catchMessageForcapturedecline(ProceedingJoinPoint pjp) throws Throwable {
	 	 System.out.println("ABORTING ALL");
	 }

	 @Around(value = "captureapproved()")
	 public void catchMessageForcaptureapprove(ProceedingJoinPoint pjp) throws Throwable {
	 	 System.out.println("ABORTING ALL");
	 }

}