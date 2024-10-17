package com.example.scheduleproject.service;

import com.example.scheduleproject.dto.ScheduleRequestDTO;
import com.example.scheduleproject.dto.ScheduleResponseDTO;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.exception.CustomAccessDeniedException;
import com.example.scheduleproject.jwt.JwtUtil;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.entity.UserRoleEnum;
import com.example.scheduleproject.exception.ResourceNotFoundException;
import com.example.scheduleproject.repository.ScheduleRepository;
import com.example.scheduleproject.repository.CommentRepository;
import com.example.scheduleproject.repository.UserRepository;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public ScheduleService(ScheduleRepository scheduleRepository, CommentRepository commentRepository,
                           UserRepository userRepository, JwtUtil jwtUtil) { // 생성자에 추가
        this.scheduleRepository = scheduleRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository; // UserRepository 초기화
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Schedule save(ScheduleRequestDTO requestDTO) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdDate = requestDTO.getCreatedDate() != null ? requestDTO.getCreatedDate() : now;
        LocalDateTime modifiedDate = requestDTO.getModifiedDate() != null ? requestDTO.getModifiedDate() : now;

        User author = new User();
        author.setUsername(requestDTO.getAuthor()); // 작성자 설정
        userRepository.save(author); // User 엔티티 저장

        Schedule schedule = new Schedule(
                requestDTO.getTitle(),
                requestDTO.getContent(),
                author, // User 객체로 전달
                createdDate,
                modifiedDate
        );
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Page<Schedule> findAll(Pageable pageable) {
        return scheduleRepository.findAll(pageable);
    }

    public Schedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 일정이 존재하지 않습니다. ID: " + id));
    }

    @Transactional
    public void deleteById(Long id, String token) {
        try {
            // 토큰에서 사용자 정보 가져오기
            String username = jwtUtil.getUserInfoFromToken(token).getSubject();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));

            // 사용자 권한 확인
            if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
                throw new AccessDeniedException("권한이 없습니다."); // 사용자 권한이 없을 경우 예외 처리
            }

            // 스케줄 삭제 로직
            scheduleRepository.deleteById(id);
        } catch (AccessDeniedException e) {
            throw new CustomAccessDeniedException("Access Denied: " + e.getMessage()); // 사용자 정의 예외 처리
        }
    }

    public Schedule updateSchedule(Long id, ScheduleRequestDTO scheduleRequestDTO) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + id));

        existingSchedule.setTitle(scheduleRequestDTO.getTitle());
        existingSchedule.setContent(scheduleRequestDTO.getContent());

        return scheduleRepository.save(existingSchedule);
    }

    public Page<ScheduleResponseDTO> findAllWithPaging(Pageable pageable) {
        return scheduleRepository.findAll(pageable)
                .map(schedule -> {
                    Long commentCount = commentRepository.countByScheduleId(schedule.getId());
                    return new ScheduleResponseDTO(
                            schedule.getId(),
                            schedule.getTitle(),
                            schedule.getContent(),
                            commentCount,
                            schedule.getCreatedDate(),
                            schedule.getModifiedDate(),
                            schedule.getAuthor() != null ? schedule.getAuthor().getUsername() : "작성자 없음" // null 체크
                    );
                });
    }
}
