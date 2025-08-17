package com.example.sulmuro_app.exception;

public class BusinessException extends RuntimeException {
    private final ApiErrorCode errorCode;

    public BusinessException(ApiErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }
}
