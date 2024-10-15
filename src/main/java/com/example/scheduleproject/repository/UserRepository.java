package com.example.scheduleproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.scheduleproject.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

