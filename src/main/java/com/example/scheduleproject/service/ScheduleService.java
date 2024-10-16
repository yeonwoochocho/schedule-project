package com.example.scheduleproject.service;

import com.example.scheduleproject.dto.ScheduleRequestDTO;
import com.example.scheduleproject.dto.ScheduleResponseDTO;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.exception.ResourceNotFoundException;
import com.example.scheduleproject.repository.ScheduleRepository;
import com.example.scheduleproject.repository.CommentRepository;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, CommentRepository commentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Schedule save(ScheduleRequestDTO requestDTO) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdDate = requestDTO.getCreatedDate() != null ? requestDTO.getCreatedDate() : now;
        LocalDateTime modifiedDate = requestDTO.getModifiedDate() != null ? requestDTO.getModifiedDate() : now;

        User author = new User();
        author.setUsername(requestDTO.getAuthor()); // 작성자 설정

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
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
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
