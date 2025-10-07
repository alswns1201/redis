package com.spring.redis.service.email;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final RedissonClient redissonClient;

    @Async("EmailExecutor") // 이메일 전송 작업을 별도의 스레드에서 실행
    public CompletableFuture<String> sendEmail(String email) { // 반환 타입을 String으로 변경하여 결과 메시지를 전달
        String lockKey = "lock:email:" + email;
        RLock lock = redissonClient.getLock(lockKey); // 이메일 주소별로 락 생성

        String resultMessage;
        try {
            // 락을 획득하되, 기다리지 않고 즉시 시도 (0초 대기)
            // 락 획득에 성공하면 10초 후에 자동으로 락이 해제되도록 설정
            if (lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " - 이메일 전송 시작: " + email);
                    Thread.sleep(2000); // 이메일 전송 시뮬레이션 (2초 소요)
                    System.out.println(Thread.currentThread().getName() + " - 이메일 전송 완료: " + email);
                    resultMessage = "이메일 전송 성공: " + email;
                } finally {
                    // 락이 현재 스레드에 의해 보유 중인 경우에만 해제
                    // (autoUnlock 기능이 있어도 명시적으로 해제하는 것이 안전)
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                // 락 획득 실패 (다른 스레드/서버에서 이미 처리 중)
                System.out.println(Thread.currentThread().getName() + " - 이미 락이 걸려 있어 이메일 전송 스킵: " + email);
                resultMessage = "이메일 전송 스킵 (이미 처리 중): " + email;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMessage = "이메일 전송 중 오류 발생: " + e.getMessage();
        }

        return CompletableFuture.completedFuture(resultMessage);
    }
}
