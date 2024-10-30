package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.CommentRequestDTO;
import com.example.scheduleproject.entity.Comment;
import com.example.scheduleproject.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Comment> addComment(
            @PathVariable Long scheduleId,
            @Valid @RequestBody CommentRequestDTO requestDTO) {
        Comment savedComment = commentService.saveComment(scheduleId, requestDTO);
        return ResponseEntity.status(201).body(savedComment); // 201 Created 반환
    }

    // 2. 일정에 대한 댓글 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<List<Comment>> getCommentsBySchedule(@PathVariable Long scheduleId) {
        List<Comment> comments = commentService.findCommentsByScheduleId(scheduleId);
        return ResponseEntity.ok(comments); // 200 OK 반환
    }

    // 3. 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }
}
