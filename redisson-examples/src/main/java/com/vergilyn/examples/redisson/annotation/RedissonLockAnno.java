package com.vergilyn.examples.redisson.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.redisson.RedissonLock;
import org.redisson.config.Config;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLockAnno {

    LockType type() default LockType.BASIC_LOCK;
    /**
     * 锁对象名
     */
    String prefix() default "";

    /**
     * 获取锁的最长等待时间; 非空且大于0
     */
    long waitTime() default 5;

    /**
     * 锁释放时间; -1时默认设置为30s, 详见源码实现:{@link RedissonLock#tryAcquireAsync(long, TimeUnit, long)}、{@link Config#lockWatchdogTimeout}
     */
    long leaseTime() default -1;

    TimeUnit unit() default TimeUnit.SECONDS;

    enum LockType {
        BASIC_LOCK, FAIR_LOCK
    }
}
