package com.library.feign;

import com.library.NaverBookResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = NaverClientTest.TestConfig.class)
@ActiveProfiles("test")
class NaverClientTest {

    @EnableAutoConfiguration
    @EnableFeignClients(clients = NaverClient.class)
   // @ComponentScan(basePackageClasses = NaverClient.class)
    public static class TestConfig {}

    @Autowired
    NaverClient naverClient;

    @Test
    public void callNaver() throws Exception {
        String bookName = "HTTP";
        NaverBookResponse response = naverClient.search(bookName, 1, 10);
        System.out.println("response = " + response);
        assertThat(response).isNotNull();
    }

}

/*
참고) @ComponentScan(basePackageClasses = NaverClient.class) 없이 어떻게 실행되는 걸까? => @EnableFeignClients(clients = NaverClient.class)가 스캔 역할을 하기 때문.
- @EnableFeignClients(clients = NaverClient.class) 자체가 @ComponentScan 역할을 하기 때문에, 별도로 @ComponentScan(basePackageClasses = NaverClient.class)를 안 해도 정상 동작한다.
  - @FeignClient는 내부적으로 @Import(FeignClientsRegistrar.class)를 통해 처리됨
- @EnableFeignClients는 자동으로 컴포넌트 스캔을 수행한다.
- @EnableFeignClients(clients = NaverClient.class) 이 코드는 다음과 같이 동작한다.
  - Spring Cloud는 NaverClient.class가 위치한 패키지를 기준으로 스캔.
  - 그 과정에서 @FeignClient로 선언된 인터페이스를 찾아, 자동으로 프록시 객체를 만들어서 구현체를 Bean으로 등록
- 즉, 이 애노테이션이 @ComponentScan을 대신하는 역할을 이미 하고 있는 것.
  - 따라서 FeignClient만 테스트할 경우는 @ComponentScan이 꼭 필요는 없음. (일반 컴포넌트 있을 때만 필요)


참고) 그럼 언제 @ComponentScan이 필요할까?
- 일반적인 @Component, @Service, @Configuration 등을 찾아야 할 때
- Feign이 아닌 일반 클래스들을 스캔해야 할 때
- 또는 테스트 컨텍스트에 특정 컴포넌트를 수동으로 등록하고 싶을 때
*/