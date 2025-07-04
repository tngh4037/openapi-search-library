package com.library.feign

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.exception.ApiException
import com.library.exception.ErrorType
import com.library.KakaoErrorResponse
import feign.Request
import feign.Response
import org.springframework.http.HttpStatus
import spock.lang.Specification

class KakaoErrorDecoderTest extends Specification {

    ObjectMapper objectMapper = Mock()
    KakaoErrorDecoder errorDecoder = new KakaoErrorDecoder(objectMapper)

    def "에러 디코더에서 에러발생시 ApiException 예외가 throw 된다."() {
        given:
        def responseBody = Mock(Response.Body)
        def inputStream = new ByteArrayInputStream()
        def response = Response.builder()
                .status(400)
                .request(Request.create(Request.HttpMethod.GET, "testUrl", [:], null as Request.Body, null))
                .body(responseBody)
                .build()

        // mocking
        and:
        1 * responseBody.asInputStream() >> inputStream
        1 * objectMapper.readValue(*_) >> new KakaoErrorResponse("InvalidArgument", "Size is more than max")

        when:
        errorDecoder.decode(_ as String, response)

        then:
        ApiException e = thrown()
        verifyAll {
            e.errorMessage == "Size is more than max"
            e.httpStatus == HttpStatus.BAD_REQUEST
            e.errorType == ErrorType.EXTERNAL_API_ERROR
        }
    }
}
