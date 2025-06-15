package com.library;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/test")
public class AsyncTestController {

    private final RestClient restClient = RestClient.create();

    @GetMapping
    public void test() {

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int requestId = i;

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {

                    ResponseEntity<String> result = restClient.get()
                            .uri("http://localhost:8080/v1/books?query=java&page=1&size=2")
                            .retrieve()
                            .toEntity(String.class);
                    log.info("result = {}, requestId = {}", result, requestId);
                } catch (Exception e) {
                    log.error("실패!!! requestId = {}", requestId, e);
                }
            });

            futures.add(future);
        }

        CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .join();
    }

}
