package com.library.controller.response;

import com.library.exception.ErrorType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러응답")
public record ErrorResponse(
        @Schema(description = "에러 메세지", example = "잘못된 요청입니다")
        String errorMessage,
        @Schema(description = "에러 타입", example = "INVALID_PARAMETER")
        ErrorType errorType
) {
}
