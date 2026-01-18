package com.yupi.yuaicodemother.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限检查注解
 * 用于在方法级别进行权限验证，确保只有具有特定角色的用户才能访问被注解的方法
 * 
 * 使用 RetentionPolicy.RUNTIME 保留策略，使得该注解在运行时可通过反射获取，
 * 以便在AOP切面或拦截器中进行权限检查
 */
@Target(ElementType.METHOD) // 指定此注解只能应用于方法
@Retention(RetentionPolicy.RUNTIME) // 指定此注解在运行时保留，可通过反射获取
public @interface AuthCheck {

    /**
     * 必须有某个角色
     * 定义此注解的属性，指定访问该方法必须具备的角色
     * 默认值为空字符串，表示不强制要求特定角色
     *
     * @return 角色名称，如 "admin"、"user" 等
     */
    String mustRole() default "";
}
