package com.library;

import com.library.feign.KakaoClient;
import com.library.feign.NaverClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync // @Async 를 하려면 @EnableAsync 를 적용해주어야 한다.
@EnableFeignClients(clients = {NaverClient.class, KakaoClient.class})
@SpringBootApplication
public class LibrarySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySearchApplication.class, args);
    }
}
