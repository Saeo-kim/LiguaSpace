package com.example.linguaspace.korean;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "korean_entries")
public class KoreanEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(length = 100)
    private String source;

    @Column(length = 5000)
    private String note;

    @Column(length = 240)
    private String question;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected KoreanEntry() {
    }

    public KoreanEntry(
            String title,
            String type,
            String source,
            String note,
            String question
    ) {
        this.title = title;
        this.type = type;
        this.source = source;
        this.note = note;
        this.question = question;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getNote() {
        return note;
    }

    public String getQuestion() {
        return question;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}