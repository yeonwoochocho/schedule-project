package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.CommentRequestDTO;
import com.example.scheduleproject.entity.Comment;
import com.example.scheduleproject.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 1. 댓글 추가
    @PostMapping("/{scheduleId}")
    public Comment addComment(
            @PathVariable Long scheduleId,
            @Valid @RequestBody CommentRequestDTO requestDTO) {
        return commentService.saveComment(scheduleId, requestDTO);
    }

    // 2. 일정에 대한 댓글 조회
    @GetMapping("/{scheduleId}")
    public List<Comment> getCommentsBySchedule(@PathVariable Long scheduleId) {
        return commentService.findCommentsByScheduleId(scheduleId);
    }

    // 3. 댓글 삭제
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
    }
}
