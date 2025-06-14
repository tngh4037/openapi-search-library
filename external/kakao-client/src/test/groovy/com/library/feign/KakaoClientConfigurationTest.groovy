package com.library.feign

import feign.RequestTemplate
import spock.lang.Specification

class KakaoClientConfigurationTest extends Specification {

    KakaoClientConfiguration configuration

    void setup() {
        configuration = new KakaoClientConfiguration();
    }

    def "requestInterceptor의 header에 key값들이 적용된다."() {
        given:
        def template = new RequestTemplate()
        def restApiKey = "key"

        and: "interceptor 를 타기전에는 header가 존재하지 않는다."
        template.headers()["Authorization"] == null

        when: "interceptor 를 탄다."
        def interceptor = configuration.requestInterceptor(restApiKey)
        interceptor.apply(template)

        then: "interceptor 를 탄 이후에는 header 가 추가된다."
        template.headers()["Authorization"].contains("KakaoAK " + restApiKey)
    }
}
