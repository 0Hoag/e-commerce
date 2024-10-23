package com.example.product_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    EMAIL_EXISTED(1008, "Email existed, please choose another one", HttpStatus.BAD_REQUEST),
    USERNAME_IS_MISSING(1009, "Username missing, please enter username", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1010, "Username existed, please choose another one", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1011, "User not existed", HttpStatus.BAD_REQUEST),
    USER_NOT_HAVE_PROFILE(1011, "User not have profile", HttpStatus.BAD_REQUEST),

    OUT_OF_STOCK_PRODUCT(1012, "Out of stock product", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(1013, "Product not existed!", HttpStatus.BAD_REQUEST),
    AUTHOR_NOT_EXISTED(1014, "Author not existed!", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1015, "Category not existed!", HttpStatus.BAD_REQUEST),
    CATEGORY_DAD_NOT_EXISTED(1015, "Category dad not existed!", HttpStatus.BAD_REQUEST),
    UPLOAD_FILE_FAIL(9999, "Upload File to faid", HttpStatus.BAD_REQUEST)

            ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
