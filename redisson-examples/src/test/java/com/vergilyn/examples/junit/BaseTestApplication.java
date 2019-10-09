package com.vergilyn.examples.junit;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/2/11
 */
@SpringBootApplication
public class BaseTestApplication {

    @Bean
    public RedissonClient redissonClient() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("config/single-server-config.json");
        Config config = Config.fromJSON(classPathResource.getFile());
        return Redisson.create(config);
    }


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BaseTestApplication.class);
        application.run(args);
    }
}
