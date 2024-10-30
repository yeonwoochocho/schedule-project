package com.example.scheduleproject.exception;

import com.example.scheduleproject.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

// 글로벌 예외 처리기
//권한이 없는 사용자가 요청할 경우 403 상태 코드를 반환
@ControllerAdvice
public class GlobalExceptionHandler {

    // 403 Forbidden - 권한 없는 접근 처리
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(
            ForbiddenException ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "ERR002", "권한이 없습니다.");
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.FORBIDDEN);
    }

    // 400 Bad Request - 잘못된 요청 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(
            IllegalArgumentException ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "ERR001", "요청값의 형식이 맞지 않습니다.") {

        };
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }
    // 500 Internal Server Error - 서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalServerError(
            Exception ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "ERR500", "네트워크 요청에 실패했습니다. 다시 시도해주시기 바랍니다.");
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
