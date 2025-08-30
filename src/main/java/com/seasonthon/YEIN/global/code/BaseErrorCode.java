package com.seasonthon.YEIN.global.code;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    boolean isSuccess();
    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();

    default <T> ApiResponse<T> toResponse(T data) {
        return ApiResponse.<T>builder()
                .success(isSuccess())
                .code(getCode())
                .message(getMessage())
                .data(data)
                .build();
    }
}
