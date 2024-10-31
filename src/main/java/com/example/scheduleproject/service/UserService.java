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


    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());


        User user = new User(
                userRequestDTO.getUsername(),
                userRequestDTO.getEmail(),
                encodedPassword,
                userRequestDTO.getRole());
        userRepository.save(user);


        String token = jwtUtil.createToken(user.getUsername(), user.getRole());


        return new UserResponseDTO(user, token);
    }



    public String login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 username 또는 password입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("유효하지 않은 username 또는 password입니다.");
        }


        return jwtUtil.createToken(user.getUsername(), user.getRole());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( id+"유저를 찾을 수 없습니다. "));
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
