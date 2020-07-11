package com.fj.log.annotation;

import com.fj.log.AutoLogAspect;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 日志组件启用
 * @author xpf
 * @date 2020/6/21 19:03
 */
@Import(value = AutoLogAspect.class)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableAutoLog {
}
