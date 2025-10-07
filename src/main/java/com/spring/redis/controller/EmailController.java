package com.spring.redis.controller;

import com.spring.redis.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send-emails-nonblocking")
    public CompletableFuture<List<String>> sendEmailsNonBlocking(@RequestParam("emails") List<String> email) {
        CompletableFuture<String> future1 = emailService.sendEmail(email.get(0));
        CompletableFuture<String> future2 = emailService.sendEmail(email.get(1));
        CompletableFuture<String> future3 = emailService.sendEmail(email.get(2));

        // 모든 CompletableFuture가 완료될 때 비동기적으로 List<String>을 반환
        return CompletableFuture.allOf(future1, future2, future3)
                .thenApply(v -> Stream.of(future1, future2, future3)
                        .map(CompletableFuture::join) // 여기서는 이미 allOf가 완료되었으므로 join은 즉시 반환됩니다.
                        .collect(Collectors.toList()));
    }
}
