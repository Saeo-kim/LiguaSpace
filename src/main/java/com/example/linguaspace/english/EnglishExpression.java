package com.example.linguaspace.english;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "english_expressions", indexes = @Index(name = "idx_english_expressions_created_at", columnList = "createdAt"))
public class EnglishExpression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String expression;

    @Column(length = 300)
    private String meaning;

    @Column(length = 500)
    private String example;

    @Column(length = 40)
    private String tag;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected EnglishExpression() {
    }

    public EnglishExpression(String expression, String meaning, String example, String tag) {
        apply(expression, meaning, example, tag);
    }

    public void apply(String expression, String meaning, String example, String tag) {
        this.expression = expression;
        this.meaning = meaning;
        this.example = example;
        this.tag = tag;
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
    public String getExpression() { return expression; }
    public String getMeaning() { return meaning; }
    public String getExample() { return example; }
    public String getTag() { return tag; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
