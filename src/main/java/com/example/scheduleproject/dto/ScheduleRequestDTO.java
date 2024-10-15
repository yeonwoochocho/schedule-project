package com.example.scheduleproject.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 일정 생성 및 수정 요청을 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {

    @NotBlank(message = "할일 제목은 비어있을 수 없습니다.")
    @Size(max = 10, message = "할일 제목은 최대 10글자까지 가능합니다.")
    private String title;

    @NotBlank(message = "할일 내용은 비어있을 수 없습니다.")
    private String content;

    @NotBlank(message = "작성자명은 비어있을 수 없습니다.")
    @Size(max = 4, message = "작성자명은 최대 4글자까지 가능합니다.")
    private String author;

    @FutureOrPresent(message = "작성일은 과거가 될 수 없습니다.")
    private LocalDateTime createdDate = LocalDateTime.now();

    @FutureOrPresent(message = "수정일은 과거가 될 수 없습니다.")
    private LocalDateTime modifiedDate = LocalDateTime.now();
}

