package com.library.feign

import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.test.context.ActiveProfiles
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
@SpringBootTest(classes = NaverClientSpockTest.TestConfig.class)
@ActiveProfiles("test")
class NaverClientSpockTest extends Specification {

    @EnableAutoConfiguration
    @EnableFeignClients(clients = NaverClient.class)
    public static class TestConfig {}

    @Autowired
    NaverClient naverClient;

    def "naver 호출"() {
        given:

        when:
        def response = naverClient.search("HTTP", 1, 10)

        then:
        print response
    }

}
