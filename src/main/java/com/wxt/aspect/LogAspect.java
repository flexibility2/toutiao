package com.wxt.aspect;

import jdk.nashorn.internal.scripts.JO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.wxt.controller.IndexController.profile(..))")
    public void beforeMethod(JoinPoint joinPoint)
    {
        StringBuilder sb =new StringBuilder();
        for (Object obj: joinPoint.getArgs())
        {
            sb.append("arg: " + obj.toString() + " ]");
        }
        logger.info("===================before " +  sb);
    }

    @After("execution(* com.wxt.controller.IndexController.profile(..))")
    public void afterMethod()
    {
        logger.info("===================after");
    }

}
