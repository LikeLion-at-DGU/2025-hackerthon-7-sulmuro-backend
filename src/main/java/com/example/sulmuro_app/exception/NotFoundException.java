package com.example.sulmuro_app.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(ApiErrorCode.NOT_FOUND, message);
    }
    public NotFoundException() {
        super(ApiErrorCode.NOT_FOUND, "리소스를 찾을 수 없습니다.");
    }

}
