package com.example.sulmuro_app.dto.bin;

public class ApiResponse<T> {
    private final String message;
    private final int code;
    private final T data;

    private ApiResponse(String message, int code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message,200,data);
    }
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(message,code,null);
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }
}

