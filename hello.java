package com.itheima.ssm.aop;

import com.itheima.ssm.domain.SysLog;
import com.itheima.ssm.service.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@Aspect
/**
 * 切面类
 */
public class LogAOP {
    /**
     * 注解依赖注入
     */
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SysLogService sysLogService;
    /**
     * 环绕通知，Spring提供通过编码的方式，增强切入点方法
     */
    @Around("execution(* com.itheima.ssm.controller.*.*(..))")
    public Object addLog(ProceedingJoinPoint joinPoint) {
        //调用切入点方法
        try {

            //访问时间，登录用户用户名,ip地址，controller方法的url,controller方法执行时长,方法的全限定名
            //System.out.println("controller方法执行了");
            //访问时间
            Date visitTime = new Date();
            //登录用户用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //ip地址
            String ip = request.getRemoteAddr();
            //controller方法的url
            String requestURI = request.getRequestURI();
            long start = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            long end = System.currentTimeMillis();
            //controller方法执行时长
            long excutionTime = end - start;

            //获取切入点方法名
            String methodName = joinPoint.getSignature().getName();
            //如何获取Controller方法的全类名
            //目标对象
            Object target = joinPoint.getTarget();
            //Controller类的全类名
            String className = target.getClass().getName();

            SysLog sysLog = new SysLog();
            //封装sysLog
            sysLog.setVisitTime(visitTime);
            sysLog.setUsername(username);
            sysLog.setIp(ip);
            sysLog.setUrl(requestURI);
            sysLog.setExecutionTime(excutionTime);
            sysLog.setMethod(className + "." + methodName);
            sysLogService.save(sysLog);
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }
}
