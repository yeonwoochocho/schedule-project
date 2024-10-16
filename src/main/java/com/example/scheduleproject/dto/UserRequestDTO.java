package com.example.scheduleproject.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 유저 생성 및 수정 요청을 위한 DTO 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "유저명은 비어있을 수 없습니다.")
    @Size(max = 10, message = "유저명은 최대 10글자까지 가능합니다.")
    private String username;
    @NotBlank(message = "이메일은 비어있을 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    private String password;
    @PastOrPresent(message = "작성일은 미래가 될 수 없습니다.")
    private LocalDateTime createdDate = LocalDateTime.now();

    @PastOrPresent(message = "수정일은 미래가 될 수 없습니다.")
    private LocalDateTime modifiedDate = LocalDateTime.now();
}

