package com.fj.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fj.log.annotation.AutoLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import java.util.UUID;

/**
 * 日志处理
 *
 * @author xpf
 * @date 2018/9/29 下午10:29
 */
@Aspect
@Order(1)
public class AutoLogAspect {

    private final String REQUEST_ID = "REQUEST_ID";
    private final String REQUEST_TYPE = "REQUEST_TYPE";

    private final JsonMapper jsonMapper = new JsonMapper();

    @Around(value = "@annotation(autoLog)")
    public Object around(ProceedingJoinPoint joinPoint, AutoLog autoLog) {
        long beginTime = System.currentTimeMillis();
        Signature st = joinPoint.getSignature();
        Logger log = LoggerFactory.getLogger(st.getDeclaringTypeName());
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_TYPE, autoLog.name());
        log.debug("开始处理|{}", toJson(joinPoint.getArgs()));
        Object response = null;
        try {
            response = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            log.debug("处理结束|{}|{}ms", toJson(response), endTime-beginTime);
            MDC.clear();
        }
        return response;
    }

    private String toJson(Object o) {
        try {
            return jsonMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
