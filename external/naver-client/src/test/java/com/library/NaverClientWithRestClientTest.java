package com.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

// naver-client 모듈 자체는 @SpringBootApplication 이 없다. 따라서 빈을 주입할 수 없다.
@SpringBootTest(classes = NaverClientWithRestClientTest.TestConfig.class) // @SpringBootTest가 제대로 동작하려면, 어딘가에 @SpringBootApplication 또는 최소한 @Configuration + @ComponentScan 등을 조합한 수동 설정 클래스가 필요
@ActiveProfiles("test")
class NaverClientWithRestClientTest {

    @ComponentScan(basePackageClasses = NaverClientWithRestClient.class) // NaverClientWithRestClient 를 스캔할수있도록 지정 ( NaverClientWithRestClient 클래스가 위치한 패키지를 스캔해서 Bean 등록 )
    static class TestConfig { }

    @Autowired
    NaverClientWithRestClient naverClientWithRestClient;

    @Test
    public void callNaver() throws Exception {
        String bookName = "HTTP";
        String response = naverClientWithRestClient.search(bookName);
        System.out.println("response = " + response);
    }
}

/*
참고) Spring 관련 의존성은 실제로 naver-client 에도 들어 있다.
- naver-client가 library-search에 포함되고, 그 library-search는 Spring Boot 의존성이 있기 때문에, IDE에서 자동으로 Spring 클래스를 인식해서 import는 가능해 보일 수 있음.
- 하지만 naver-client 를 단독으로 (스프링을 이용해서) 테스트하거나 실행할 경우 에러가 발생한다. (ClassNotFoundException 또는 NoClassDefFoundError 등)
*/

/* 참고) @SpringBootTest 기본 동작 방식
: @SpringBootTest는 기본적으로 다음 순서로 동작합니다.

- 클래스 경로 상에서 @SpringBootApplication을 찾음 (보통 최상위 메인 클래스)
- 찾으면 그걸 기준으로 스프링 컨텍스트 구성
- 없으면 @SpringBootTest(classes = ...)로 명시해줘야 함 -> 위와 같은 방식임. ( 이렇게 직접 수동 구성을 제공해줘야 Spring Boot가 컨텍스트를 만들 수 있습니다. )
- 참고) @SpringBootTest만 달고 @Configuration도 @SpringBootApplication도 없는 경우
  - 실행 시 다음과 같은 에러 발생
  - IllegalStateException: Unable to find a @SpringBootConfiguration, you need to use @SpringBootTest(classes=...)
- 정리) @SpringBootTest를 쓰려면 Spring Boot 컨텍스트를 구성할 클래스가 반드시 있어야 합니다.
  - 그게 @SpringBootApplication이거나, 수동으로 만든 @Configuration 클래스면 됩니다.
*/

/*
고민) 여기서 스프링 테스트를 하는게 맞을까?
- 테스트 실행 시에는 테스트가 포함된 모듈(상위 모듈)의 클래스패스가 기준.
- 이때 Spring Boot 관련 클래스들 (@SpringBootTest, @Component, RestClient 등)은 상위 모듈에서 resolve됨.
- 따라서 naver-client에 명시적으로 starter가 없어도, 빌드와 테스트가 성공할 수 있음.

- 장점:
  - naver-client는 Spring에 덜 의존적이고,
  - 재사용성(예: 다른 프레임워크나 유닛 테스트에서) 유지 가능
  - 테스트는 Spring Boot 환경에서 실행되므로, 실제 환경과 유사하게 동작

- 단점:
  - Spring에 명시적 의존이 없는데 실제로는 의존하고 있음 -> 코드 명세와 실행 환경이 어긋남
  - 팀이나 IDE 입장에서는 혼란을 줄 수 있음 (e.g. 어떤 동료가 naver-client만 IDE로 열었을 때 Spring이 없다고 생각할 수 있음)
  - Spring 버전 차이가 생기면 하위 모듈과 상위 모듈 간 충돌 가능성 존재

- 추천 방향
  - 1) naver-client는 Spring 없이 유지
    - 순수 Java 클라이언트로 관리하고, 테스트는 search-api에서하자. ( 추천 (구조적 분리 명확) )
  - 2) 여기서 할거라면, 명확성을 위해 naver-client에 Spring 명시
    - Spring 기능 많이 쓴다면 명시적으로 starter 추가 ( 가능 (명확성 유지) )
  - 참고) 현재 구조는 상위 모듈(Spring 포함)이 테스트를 도와주는 상황이라고 볼수있음. 따라서 그대로 써도 되지만, 명시적이지 않다는 점만 조심, 유의하자.
*/
