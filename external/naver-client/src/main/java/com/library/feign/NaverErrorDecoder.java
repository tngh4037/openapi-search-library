package com.library.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.ApiException;
import com.library.ErrorType;
import com.library.NaverErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class NaverErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public NaverErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) { // methodKey : feign 이 어떤 메서드를 호출하다가 실패했는지에 대한 메서드 명
        try {
            // responseBody를 읽어서 어떤 에러가 발생했는지에 대한 정보 확인 가능
            String responseBody = new String(
                    response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

            NaverErrorResponse errorResponse =
                    objectMapper.readValue(responseBody, NaverErrorResponse.class);

            throw new ApiException(
                    errorResponse.getErrorMessage(),
                    ErrorType.EXTERNAL_API_ERROR,
                    HttpStatus.valueOf(response.status()));
        } catch (IOException e) {
            log.error("[Naver] 에러 메세지 파싱 에러 code={}, request={}, methodKey={}, errorMessage={}",
                    response.status(), response.request(), methodKey, e.getMessage());

            throw new ApiException(
                    "네이버 메시지 파싱 에러",
                    ErrorType.EXTERNAL_API_ERROR,
                    HttpStatus.valueOf(response.status()));
        }
    }
}
