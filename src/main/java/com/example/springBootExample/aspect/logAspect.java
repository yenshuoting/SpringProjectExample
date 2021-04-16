package com.example.springBootExample.aspect;

import java.util.Arrays;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class logAspect {
	@Before("execution(* com.example.springBootExample.*.*.*(..))")
	public void before(JoinPoint joinPoint) {	
		Object target = joinPoint.getTarget();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		Logger.getLogger(target.getClass().getName())
		      .info(String.format("aop log :: %s.%s(%s)",
                target.getClass().getName(), methodName, Arrays.toString(args)));
	}
}
