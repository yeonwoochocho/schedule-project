package com.example.scheduleproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long commentCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String username; // 유저명
}
