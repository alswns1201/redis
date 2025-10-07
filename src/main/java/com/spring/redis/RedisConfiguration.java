package com.spring.redis;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class RedisConfiguration {



    @Bean
    public RedissonClient redissonClient() {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://localhost:6379"); // Redis 연결 정보
            return Redisson.create(config);
    }


    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }

    // 비동기(@Async) 처리를 위한 스레드 풀 정의
    @Bean(name = "EmailExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // 기본 스레드 수
        executor.setMaxPoolSize(10);   // 최대 스레드 수
        executor.setQueueCapacity(25); // 큐 용량
        executor.setThreadNamePrefix("EmailAsync-"); // 스레드 이름 접두사
        executor.initialize();
        return executor;
    }
}
