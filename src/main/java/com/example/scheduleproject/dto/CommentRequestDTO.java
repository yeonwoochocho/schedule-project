package com.example.scheduleproject.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
    private String content;

    @NotBlank(message = "작성자명은 비어있을 수 없습니다.")
    private String author;
}
