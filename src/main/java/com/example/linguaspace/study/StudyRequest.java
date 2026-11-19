package com.example.linguaspace.study;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_requests",
        indexes = @Index(name = "idx_study_requests_created_at", columnList = "createdAt")
)
public class StudyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String language;

    @Column(nullable = false, length = 100)
    private String goal;

    @Column(nullable = false, length = 1000)
    private String topic;

    @Column(length = 1000)
    private String difficultPart;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected StudyRequest() {
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDifficultPart(String difficultPart) {
        this.difficultPart = difficultPart;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getGoal() {
        return goal;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficultPart() {
        return difficultPart;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}