package com.example.scheduleproject.service;
import com.example.scheduleproject.dto.ScheduleRequestDTO;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.exception.ResourceNotFoundException;
import com.example.scheduleproject.repository.ScheduleRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Schedule save(ScheduleRequestDTO requestDTO) {
        // DTO를 엔티티로 변환 후 저장
        Schedule schedule = new Schedule(requestDTO.getTitle(), requestDTO.getContent(), requestDTO.getAuthor());
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
}

