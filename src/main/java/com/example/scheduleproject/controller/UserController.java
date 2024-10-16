package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.LoginRequestDTO;
import com.example.scheduleproject.dto.UserRequestDTO;
import com.example.scheduleproject.dto.UserResponseDTO;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //1. 유저 생성 (회원 가입)
    @PostMapping("/signup")
    public UserResponseDTO signup(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String token = userService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        return ResponseEntity.ok(token);
    }
    //2. 유저 전체 조회
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 3. 특정 유저 조회
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    // 4. 유저 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
