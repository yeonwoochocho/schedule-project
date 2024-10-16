package com.example.scheduleproject.service;

import com.example.scheduleproject.dto.CommentRequestDTO;
import com.example.scheduleproject.entity.Comment;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.exception.ResourceNotFoundException;
import com.example.scheduleproject.repository.CommentRepository;
import com.example.scheduleproject.repository.ScheduleRepository;
import com.example.scheduleproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, ScheduleRepository scheduleRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository; // 초기화
    }

    @Transactional
    public Comment saveComment(Long scheduleId, CommentRequestDTO requestDTO) {
        // 일정 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 일정이 존재하지 않습니다. ID: " + scheduleId));

        // 작성자 이름으로 User 조회
        User author = userRepository.findByUsername(requestDTO.getAuthor())
                .orElseThrow(() -> new ResourceNotFoundException("작성자가 존재하지 않습니다. 이름: " + requestDTO.getAuthor()));
        // 댓글 저장
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setContent(requestDTO.getContent());
        comment.setSchedule(schedule);

        return commentRepository.save(comment);
    }

    public List<Comment> findCommentsByScheduleId(Long scheduleId) {
        return commentRepository.findByScheduleId(scheduleId);
    }

    @Transactional
    public void deleteById(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("해당 댓글이 존재하지 않습니다. ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }
}
