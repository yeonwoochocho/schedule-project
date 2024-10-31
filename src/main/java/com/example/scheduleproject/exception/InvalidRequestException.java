package com.example.scheduleproject.exception;

public class InvalidRequestException extends RuntimeException {

    // 기본 생성자
    public InvalidRequestException() {
        super();
    }

    // 예외 메시지를 받는 생성자
    public InvalidRequestException(String message) {
        super(message);
    }

    // 예외 메시지와 원인을 받는 생성자
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    // 원인만 받는 생성자
    public InvalidRequestException(Throwable cause) {
        super(cause);
    }
}

