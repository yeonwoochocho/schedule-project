package com.example.scheduleproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.scheduleproject.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByScheduleId(Long scheduleId);  // 특정 일정의 댓글 조회
    // 일정 ID에 해당하는 댓글 개수 카운트 메서드
    Long countByScheduleId(Long scheduleId);
}

