package com.example.scheduleproject.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    @Size(max = 20, message = "할일 제목은 최대 20글자까지 가능합니다.")
    private String title;

    @NotBlank(message = "할일 내용은 비어있을 수 없습니다.")
    @Size(max = 500, message = "내용은 최대 500글자까지 가능합니다.")
    private String content;

    @NotBlank(message = "작성자명은 비어있을 수 없습니다.")
    @Size(max = 4, message = "작성자명은 최대 4글자까지 가능합니다.")
    private String author;

    //@FutureOrPresent(message = "작성일은 현재 또는 미래 시점이어야 합니다.")
    private LocalDateTime createdDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    //@FutureOrPresent(message = "수정일은 현재 또는 미래 시점이어야 합니다.")
    private LocalDateTime modifiedDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
}

