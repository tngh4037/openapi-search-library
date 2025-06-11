package com.library.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.NaverErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NaverErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public NaverErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // responseBody를 읽어서 어떤 에러가 발생했는지에 대한 정보 확인 가능
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String responseBody = new String(
                    response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

            NaverErrorResponse errorResponse =
                    objectMapper.readValue(responseBody, NaverErrorResponse.class);

            throw new RuntimeException(errorResponse.getErrorMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
