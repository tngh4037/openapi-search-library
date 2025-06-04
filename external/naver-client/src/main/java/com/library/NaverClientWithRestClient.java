package com.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverClientWithRestClient {

    private final RestClient restClient;
    private final String naverUrl;
    private final String naverClientId;
    private final String naverClientSecret;

    public NaverClientWithRestClient(@Value("${external.naver.url}") String naverUrl,
                                     @Value("${external.naver.headers.client-id}") String naverClientId,
                                     @Value("${external.naver.headers.client-secret}") String naverClientSecret) {
        this.restClient = RestClient.create();
        this.naverUrl = naverUrl;
        this.naverClientId = naverClientId;
        this.naverClientSecret = naverClientSecret;
    }

    public String search(String query) {
        String uriString = UriComponentsBuilder.fromHttpUrl(naverUrl + "/v1/search/book.json")
                .queryParam("query", query)
                .queryParam("display", 1)
                .queryParam("start", 1)
                .toUriString();

        return restClient.get()
                .uri(uriString)
                .header("X-Naver-Client-Id", naverClientId)
                .header("X-Naver-Client-Secret", naverClientSecret)
                .retrieve()
                .body(String.class);
    }

}

// 참고) body(String.class) 는 위치와 문맥에 따라 다르다.
// -  body(..) 가 retrieve() 앞에 있으면 요청바디, 뒤에있으면 응답바디를 의미한다.
// - 참고로 GET 요청시에는 요청바디를 지원하지 않는다.
