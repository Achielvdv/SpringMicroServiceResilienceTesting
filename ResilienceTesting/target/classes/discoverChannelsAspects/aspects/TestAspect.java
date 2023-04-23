package io.pivotal.loancheckResilience.aspects;
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

    // ADVICE TO DISCOVER THE MESSAGE CHANNELS

    @Before("@annotation(org.springframework.cloud.stream.annotation.Input)")
    public void findInputChannels(JoinPoint joinPoint) {
        System.out.println("Resilience Testing Discovery - input - " + joinPoint.getSignature().getName() +  " - " + joinPoint.toLongString());
    }

    @Before("@annotation(org.springframework.cloud.stream.annotation.Output)")
    public void findOutputChannels(JoinPoint joinPoint) {
        System.out.println("Resilience Testing Discovery - output - " + joinPoint.getSignature().getName() +  " - " + joinPoint.toLongString());
    }
}
