package com.example.scheduleproject.service;

import com.example.scheduleproject.config.PasswordEncoder;
import com.example.scheduleproject.dto.UserRequestDTO;
import com.example.scheduleproject.dto.UserResponseDTO;
import com.example.scheduleproject.entity.User;
import com.example.scheduleproject.exception.InvalidRequestException;
import com.example.scheduleproject.exception.ResourceNotFoundException;
import com.example.scheduleproject.jwt.JwtUtil;
import com.example.scheduleproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    //회원 가입

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());

        // 유저 생성 및 저장
        User user = new User(
                userRequestDTO.getUsername(),
                userRequestDTO.getEmail(),
                encodedPassword,
                userRequestDTO.getRole());
        userRepository.save(user);

        // JWT 토큰 발급
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());

        // UserResponseDTO에 유저 정보와 토큰 포함하여 반환
        return new UserResponseDTO(user, token);
    }


    // 로그인
    public String login(String username, String password) {
        // 유저 조회 및 검증
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        // JWT 생성 후 반환
        return jwtUtil.createToken(user.getUsername(), user.getRole());
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
