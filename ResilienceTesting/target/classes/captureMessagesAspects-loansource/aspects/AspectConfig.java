package io.pivotal.loancheck.aspects;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages= "io.pivotal")
@EnableAspectJAutoProxy
public class AspectConfig {
}