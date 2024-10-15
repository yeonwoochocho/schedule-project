package com.example.scheduleproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "할일 제목은 비어있을 수 없습니다.")
    private String title;

    @NotBlank(message = "할일 내용은 비어있을 수 없습니다.")
    private String content;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 기본 생성자
    public Schedule() {
    }

    // 모든 필드를 초기화하는 생성자
    public Schedule(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Schedule(@NotBlank(message = "할일 제목은 비어있을 수 없습니다.") @Size(max = 10, message = "할일 제목은 최대 10글자까지 가능합니다.") String title, @NotBlank(message = "할일 내용은 비어있을 수 없습니다.") String content, @NotBlank(message = "작성자명은 비어있을 수 없습니다.") @Size(max = 4, message = "작성자명은 최대 4글자까지 가능합니다.") String author) {
    }
}
