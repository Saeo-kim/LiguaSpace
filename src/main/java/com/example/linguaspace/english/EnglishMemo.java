package com.example.linguaspace.english;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "english_memos", indexes = @Index(name = "idx_english_memos_created_at", columnList = "createdAt"))
public class EnglishMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120)
    private String title;

    @Column(nullable = false, length = 5000)
    private String body;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected EnglishMemo() {
    }

    public EnglishMemo(String title, String body) {
        apply(title, body);
    }

    public void apply(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
