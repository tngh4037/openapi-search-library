package com.library.service;

import com.library.controller.response.PageResult;
import com.library.controller.response.SearchResponse;
import com.library.repository.BookRepository;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookQueryService {

    private final BookRepository naverBookRepository;
    private final BookRepository kakaoBookRepository;

    public BookQueryService(@Qualifier("naverBookRepository") BookRepository naverBookRepository,
                            @Qualifier("kakaoBookRepository") BookRepository kakaoBookRepository) {
        this.naverBookRepository = naverBookRepository;
        this.kakaoBookRepository = kakaoBookRepository;
    }

    @CircuitBreaker(name = "naverSearch", fallbackMethod = "searchFallback")
    public PageResult<SearchResponse> search(String query, int page, int size) {
        log.info("[BookQueryService] naver query={}, page={}, size={}", query, page, size);
        return naverBookRepository.search(query, page, size);
    }

    public PageResult<SearchResponse> searchFallback(String query, int page, int size,
                                                     Throwable throwable) { // Throwable throwable : 어떤 에러가 발생헀는지 정보가 넘어옴
        // CallNotPermittedException : circuit breaker 가 OPEN 상태가 되면 CallNotPermittedException 에러가 발생한다.
        if (throwable instanceof CallNotPermittedException) {
            return handleOpenCircuit(query, page, size);
        }

        return handleException(query, page, size, throwable);
    }

    // circuit breaker 가 OPEN 이 되면 kakao 쪽으로 요청을 보낸다.
    private PageResult<SearchResponse> handleOpenCircuit(String query, int page, int size) {
        log.warn("[BookQueryService] Circuit Breaker is opened. Fallback to kakao search");
        return kakaoBookRepository.search(query, page, size);
    }

    // 일반적인 비즈니스 에러가 발생한 경우 ( 이 경우 어떻게 처리할지는 정하면 된다. 강의에서는 일반적인 에러가 발생할 때에도 카카오를 호출하도록 했다. )
    private PageResult<SearchResponse> handleException(String query, int page, int size, Throwable throwable) {
        log.error("[BookQueryService] An error occurred! Fallback to kakao search. errorMessage={}",
                throwable.getMessage());
        return kakaoBookRepository.search(query, page, size);
    }

}
