package com.library.service

import com.library.controller.response.PageResult
import com.library.repository.KakaoBookRepository
import com.library.repository.NaverBookRepository
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 매 테스트마다 ApplicationContext 초기화
@ActiveProfiles("test")
@SpringBootTest
class BookQueryServiceItTest extends Specification {

    @Autowired
    BookQueryService bookQueryService

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry // CircuitBreaker 상태 검증

    @SpringBean
    KakaoBookRepository kakaoBookRepository = Mock()

    @SpringBean
    NaverBookRepository naverBookRepository = Mock()

    def "정상 상황에서는 Circuit의 상태가 CLOSED 이고, naver 쪽으로 호출이 들어간다."() {
        given:
        def keyword = 'HTTP'
        def page = 1
        def size = 10

        when:
        bookQueryService.search(keyword, page, size)

        then:
        1 * naverBookRepository.search(keyword, page, size) >> new PageResult<>(1, 10, 0, [])

        and:
        def circuitBreaker = circuitBreakerRegistry.getAllCircuitBreakers().stream().findFirst().get()
        circuitBreaker.state == CircuitBreaker.State.CLOSED

        and:
        0 * kakaoBookRepository.search(*_)
    }

    def "circuit이 open되면 kakao쪽으로 요청을 한다."() {
        given:
        def keyword = 'HTTP'
        def page = 1
        def size = 10
        def kakaoResponse = new PageResult<>(1, 10, 0, [])
        
        // 테스트 편의를 위해 설정 변경
        def config = CircuitBreakerConfig.custom()
                .slidingWindowSize(1)
                .minimumNumberOfCalls(1)
                .failureRateThreshold(50)
                .build()
        circuitBreakerRegistry.circuitBreaker("naverSearch", config)

        and: "naver쪽은 항상 예외가 발생한다."
        naverBookRepository.search(keyword, page, size) >> { throw new RuntimeException("error!") }

        when:
        def result = bookQueryService.search(keyword, page, size)

        then: "kakao쪽으로 Fallback 된다."
        1 * kakaoBookRepository.search(keyword, page, size) >> kakaoResponse

        and: "circuit이 OPEN된다."
        def circuitBreaker = circuitBreakerRegistry.getAllCircuitBreakers().stream().findFirst().get()
        circuitBreaker.state == CircuitBreaker.State.OPEN

        and:
        result == kakaoResponse
    }

}
