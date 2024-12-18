package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.CommentRequestDTO;
import com.example.scheduleproject.dto.ScheduleRequestDTO;
import com.example.scheduleproject.entity.Comment;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.exception.CustomAccessDeniedException;
import com.example.scheduleproject.exception.ForbiddenException;
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

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid token format.");
        }
        String username = jwtUtil.extractUsername(token);


        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        Schedule schedule = new Schedule();
        schedule.setTitle(scheduleRequestDTO.getTitle());
        schedule.setContent(scheduleRequestDTO.getContent());
        schedule.setAuthor(author);

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
    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id, @RequestHeader("Authorization") String token) {

        String jwtToken = token.replace("Bearer ", "").trim();

        if (!jwtUtil.validateToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token.");
        }



       String username = jwtUtil.extractUsername(jwtToken);


        String role = jwtUtil.extractUserRole(jwtToken);
        if (!"ADMIN".equals(role)) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }

        scheduleService.deleteById(id, jwtToken);
        return ResponseEntity.noContent().build();
    }
    //5. 일정 수정
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO,
            @RequestHeader("Authorization") String token) {


        String jwtToken = token.replace("Bearer ", "").trim();

        //토큰 파싱 후 권한 확인
        String role = jwtUtil.extractUserRole(jwtToken);
        if (!"ADMIN".equals(role)) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }

        Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleRequestDTO, jwtToken);

        return ResponseEntity.ok(updatedSchedule);

    }

    // 예외 처리 메서드
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(CustomAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }


}

