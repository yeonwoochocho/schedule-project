package com.example.scheduleproject.service;

import com.example.scheduleproject.dto.UserRequestDTO;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.exception.ResourceNotFoundException;
import com.example.scheduleproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserRequestDTO userRequestDTO) {
        // DTO를 엔티티로 변환 후 저장
        User user = new User(userRequestDTO.getUsername(), userRequestDTO.getEmail());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
