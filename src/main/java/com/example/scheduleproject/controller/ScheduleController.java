package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.CommentRequestDTO;
import com.example.scheduleproject.dto.ScheduleRequestDTO;
import com.example.scheduleproject.entity.Comment;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.service.ScheduleService;
import com.example.scheduleproject.service.CommentService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 1. 일정 생성
    @PostMapping
    public Schedule create(@Valid @RequestBody ScheduleRequestDTO requestDTO) {
        return scheduleService.save(requestDTO);
    }

    // 2. 전체 일정 조회 (페이징 X)
    @GetMapping
    public List<Schedule> getAll() {
        return scheduleService.findAll();
    }

    // 3. 페이징된 일정 조회
    @GetMapping("/paged")
    public Page<Schedule> getPagedSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedDate").descending());
        return scheduleService.findAll(pageable);
    }

    // 4. 일정 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        scheduleService.deleteById(id);
    }

    //5. 일정 수정
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO) {

        // Schedule 수정 요청
        Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleRequestDTO);

        return ResponseEntity.ok(updatedSchedule);

    }


}

