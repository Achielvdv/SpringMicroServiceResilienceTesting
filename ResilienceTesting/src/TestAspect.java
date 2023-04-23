package io.pivotal.loancheck.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("around")
@Aspect
public class TestAspect {

    public static final Logger log = LoggerFactory.getLogger(TestAspect.class);

    private int counter = 1;

    // ADVICE TO CAPTURE THE MESSAGES

    @Pointcut(value = "execution(public abstract org.springframework.messaging.MessageChannel io.pivotal.loancheck.LoanProcessor.declined())")
    private void captureDeclinedMessage() {}

    @Pointcut(value = "execution(public abstract org.springframework.messaging.MessageChannel io.pivotal.loancheck.LoanProcessor.approved())")
    private void captureApprovedMessage() {}

    @Around(value = "captureDeclinedMessage()")
    public void catchMessageForDeclined(ProceedingJoinPoint pjp) throws Throwable {
//        counter++;
//        if (counter % 2 == 0) {
//            pjp.proceed();
//            System.out.println("Resilience Testing Capturing - Message SENT: " + pjp.getSignature().getName());
//        } else {
//            System.out.println("Resilience Testing Capturing - Message ABORTED: " + pjp.getSignature().getName());
//        }
        System.out.println("ABORT ALL!!");
    }

    @Around(value = "captureApprovedMessage()")
    public void catchMessageForApproved(ProceedingJoinPoint pjp) throws Throwable {
//        counter++;
//        if (counter % 2 == 0) {
//            pjp.proceed();
//            System.out.println("Resilience Testing Capturing - Message SENT: " + pjp.getSignature().getName());
//        } else {
//            System.out.println("Resilience Testing Capturing - Message ABORTED: " + pjp.getSignature().getName());
//        }
        System.out.println("ABORT ALL!!");
    }
}
