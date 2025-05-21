package com.javafood.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định!", HttpStatus.BAD_REQUEST),
    INVALID_KEY(9998, "Key không xác định!", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1001,"Email không đúng định dạng!", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1002, "Username đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "Mật khẩu không đủ an toàn!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1004, "Username không tồn tại", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005, "Unauthenticated!", HttpStatus.UNAUTHORIZED),

    // Lỗi dữ liệu đầu vào
    INVALID_INPUT(2001, "Invalid input data!", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(2002, "Required field is missing!", HttpStatus.BAD_REQUEST),
    EXISTS_DATA(2003, "Giá trị đã tồn tại", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_DATA(2004,"Giá trị không tồn tại", HttpStatus.BAD_REQUEST),
    INPUT_EMPTY(2005, "Giá trị không được để trống", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private int code;
    private String message;
    private HttpStatus httpStatus;
}
