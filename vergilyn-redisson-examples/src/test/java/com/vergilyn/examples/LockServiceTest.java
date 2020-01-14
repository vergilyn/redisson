package com.vergilyn.examples;

import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import com.vergilyn.examples.redisson.RedissonApplication;
import com.vergilyn.examples.redisson.annotation.RedissonLockAnno;
import com.vergilyn.examples.redisson.annotation.RedissonLockKey;
import com.vergilyn.examples.redisson.service.LockService;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/2/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= RedissonApplication.class)
@Slf4j
public class LockServiceTest {

    @Resource
    LockService fairLockService;

    @Test
    public void lockTest() throws Exception {
        log.info("lockTest() >>>> begin");

        int count = 4;
        CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    String s = fairLockService.fairLock("10086", 2000);
                    log.info("current-thread-id: {}, result: {}", Thread.currentThread().getId(), s);
                } catch (Exception e){
                    log.error(e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        log.info("lockTest() >>>> await....");
        latch.await();

        log.info("lockTest() >>>> end....");

    }

    // 注解形式参考
    @RedissonLockAnno(prefix="test", type = RedissonLockAnno.LockType.BASIC_LOCK)
    public void TestAnno(@RedissonLockKey Long index, @RedissonLockKey(fields={"id", "username"}) Object obj){
    }
}
