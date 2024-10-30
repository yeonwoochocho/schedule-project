package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.CommentRequestDTO;
import com.example.scheduleproject.dto.ScheduleRequestDTO;
import com.example.scheduleproject.entity.Comment;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.exception.CustomAccessDeniedException;
import com.example.scheduleproject.exception.ResourceNotFoundException;
import com.example.scheduleproject.exception.UnauthorizedException;
import com.example.scheduleproject.jwt.JwtUtil;
import com.example.scheduleproject.repository.ScheduleRepository;
import com.example.scheduleproject.repository.UserRepository;
import com.example.scheduleproject.service.ScheduleService;
import com.example.scheduleproject.service.CommentService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final JwtUtil jwtUtil;

    private final ScheduleService scheduleService;

    public ScheduleController(UserRepository userRepository,ScheduleRepository scheduleRepository, ScheduleService scheduleService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
        this.jwtUtil = jwtUtil;
    }

    // 1. 일정 생성
    @PostMapping
    public ResponseEntity<Schedule> createSchedule( @RequestBody ScheduleRequestDTO scheduleRequestDTO,
                                    @RequestHeader("Authorization") String token) {
        // "Bearer " 부분 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid token format.");
        }
        String username = jwtUtil.extractUsername(token);

        // User 객체 조회
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        // Schedule 객체 생성
        Schedule schedule = new Schedule();
        schedule.setTitle(scheduleRequestDTO.getTitle());
        schedule.setContent(scheduleRequestDTO.getContent());
        schedule.setAuthor(author); // User 객체 설정

        // 일정 저장
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSchedule);
    }


    // 2. 전체 일정 조회 (페이징 X)
    @GetMapping
    public ResponseEntity<List<Schedule>> getAll() {
        List<Schedule> schedules = scheduleService.findAll();
        return ResponseEntity.ok(schedules);
    }

    // 3. 페이징된 일정 조회
    @GetMapping("/paged")
    public ResponseEntity<Page<Schedule>> getPagedSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedDate").descending());
        Page<Schedule> pagedSchedules = scheduleService.findAll(pageable);

        return ResponseEntity.ok(pagedSchedules);
    }

    // 4. 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // Bearer 접두어 제거
        String jwtToken = token.replace("Bearer ", "").trim();

        // 토큰 검증
        if (!jwtUtil.validateToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token.");
        }

        // 사용자 정보 확인
       String username = jwtUtil.extractUsername(jwtToken);

        // 서비스에 토큰 전달하여 삭제 수행
        scheduleService.deleteById(id, username);
        return ResponseEntity.noContent().build();
    }
    //5. 일정 수정
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO,
            @RequestHeader("Authorization") String token) {

        // 1. 토큰 파싱 후 권한 확인
        String role = jwtUtil.extractUserRole(token);
        if (!"ADMIN".equals(role)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }
        // Schedule 수정 요청
        Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleRequestDTO);

        return ResponseEntity.ok(updatedSchedule);

    }

    // 예외 처리 메서드
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(CustomAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }


}

