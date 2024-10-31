package com.example.scheduleproject.dto;

import com.example.scheduleproject.entity.User;

public class UserResponseDTO {
    private String username;
    private String email;
    private String token;

    public UserResponseDTO(User user, String token) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.token = token;
    }


    public String getUsername() {
        return username;
    }


}
