package com.example.scheduleproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(force = true)
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    private final String author;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "할일 제목은 비어있을 수 없습니다.")
    private String title;

    @NotBlank(message = "할일 내용은 비어있을 수 없습니다.")
    private String content;

    // 엔티티가 생성될 때 자동으로 현재 시간 설정
    @Column(updatable = false)
    private LocalDateTime createdDate;

    // 엔티티가 수정될 때 자동으로 시간 갱신
    private LocalDateTime modifiedDate;

    public Schedule(String title, String content, String author, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 기본 생성자
    public Schedule(String author) {
        this.author = author;
    }

    // 모든 필드를 초기화하는 생성자
    public Schedule(String author, String title, String content, User user) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.user = user;
        // createdDate와 modifiedDate는 자동으로 설정됨
    }

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Schedule(@NotBlank(message = "할일 제목은 비어있을 수 없습니다.") @Size(max = 10, message = "할일 제목은 최대 10글자까지 가능합니다.") String title, @NotBlank(message = "할일 내용은 비어있을 수 없습니다.") String content, @NotBlank(message = "작성자명은 비어있을 수 없습니다.") @Size(max = 4, message = "작성자명은 최대 4글자까지 가능합니다.") String author, String author1) {
        this.author = author1;
    }

    // 불필요한 생성자는 제거
    // 새로 추가할 생성자에서 createdDate 및 modifiedDate를 수동으로 설정할 필요 없음
}
