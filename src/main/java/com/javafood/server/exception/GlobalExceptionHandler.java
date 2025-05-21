package com.javafood.server.exception;

import com.javafood.server.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handleException(RuntimeException ex){
        ApiResponse response = ApiResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(Arrays.toString(ex.getStackTrace())).build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex){
        String enumKey = ex.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {

        }

        ApiResponse response = ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException ex){
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse response = ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex){
        log.info(ex.getMessage());
        ApiResponse response = ApiResponse.builder().code(401).message(ex.getMessage()).build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.info("Lỗi phân quyền: {}", ex.getMessage());
        ApiResponse response = ApiResponse.builder()
                .code(HttpStatus.FORBIDDEN.value()) // 403
                .message("Truy cập bị từ chối: Bạn không có quyền truy cập tài nguyên này.")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
