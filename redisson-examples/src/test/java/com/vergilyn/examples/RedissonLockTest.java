package com.vergilyn.examples;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.vergilyn.examples.redisson.RedissonApplication;
import com.vergilyn.examples.redisson.exception.RedissonException;
import com.vergilyn.examples.redisson.template.client.FairLockClient;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= RedissonApplication.class)
@Slf4j
public class RedissonLockTest {
	ExecutorService executorService = Executors.newFixedThreadPool(10);
	@Resource
    FairLockClient fairLockClient;
	
	@Test
	public void basicTest(){
		int count = 40;
		CountDownLatch latch = new CountDownLatch(count);
		
		for (int i = 0; i < count; i++) {
			executorService.submit(() -> lock(latch));
		}

		try {
			latch.await();
			log.info("basicTest() execute finish....");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTemplate(){
		int count = 4;
		final CountDownLatch latch = new CountDownLatch(count);
		
		for (int i = 0; i < count; i++) {
			executorService.submit(() -> fairLockClient.tryTemplate("test", 100, 30, TimeUnit.SECONDS, () -> {
				log.info("lock....");

				try {
					new Semaphore(0).tryAcquire(5 , TimeUnit.SECONDS);  // 代码执行5s
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					latch.countDown();
					log.info("await release lock....");
				}

				return null;
			}));
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 异步获取锁参考
	public void asyncTemplate(){
		RLock rLock = fairLockClient.newInstance("test");
		RFuture<Boolean> booleanRFuture = rLock.tryLockAsync(10, 30, TimeUnit.SECONDS);

		// 获取锁之前, 不需要锁的逻辑代码...
		// code(忽略)

		try {
			booleanRFuture.await();
			if(booleanRFuture.get()){ // true: 成功获取锁
				// 获取锁之后的逻辑代码

				// some code

				rLock.unlock();
				// rLock.unlockAsync();
			}

			throw new RedissonException("获取锁失败, 可能原因: 等待获取锁超时, lock: test ");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();

		} finally {
			if(rLock.isHeldByCurrentThread()){
				rLock.unlock();
			}
		}

	}

	private void lock(CountDownLatch latch){
		log.info("await lock....");

		RLock fairLock = fairLockClient.newInstance("test");
		fairLock.lock(10, TimeUnit.SECONDS);
		log.info("lock....");

		try {
			new Semaphore(0).tryAcquire(5 , TimeUnit.SECONDS);  // 代码执行5s
		} catch (InterruptedException e) {
			// do nothing
		} finally {
			fairLock.unlock();
			latch.countDown();
			log.info("unlock....");
		}
	}
}
