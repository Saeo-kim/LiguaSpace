package com.example.linguaspace.english;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "english_words", indexes = @Index(name = "idx_english_words_created_at", columnList = "createdAt"))
public class EnglishWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String word;

    @Column(length = 120)
    private String pronunciation;

    @Column(nullable = false, length = 300)
    private String meaning;

    @Column(length = 500)
    private String example;

    @Column(length = 500)
    private String exampleTranslation;

    @Column(length = 1000)
    private String note;

    @Column(length = 40)
    private String category;

    @Column(nullable = false)
    private boolean learned;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected EnglishWord() {
    }

    public EnglishWord(String word, String pronunciation, String meaning, String example,
                       String exampleTranslation, String note, String category) {
        apply(word, pronunciation, meaning, example, exampleTranslation, note, category);
    }

    public void apply(String word, String pronunciation, String meaning, String example,
                      String exampleTranslation, String note, String category) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.meaning = meaning;
        this.example = example;
        this.exampleTranslation = exampleTranslation;
        this.note = note;
        this.category = category;
    }

    public void setLearned(boolean learned) {
        this.learned = learned;
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
    public String getWord() { return word; }
    public String getPronunciation() { return pronunciation; }
    public String getMeaning() { return meaning; }
    public String getExample() { return example; }
    public String getExampleTranslation() { return exampleTranslation; }
    public String getNote() { return note; }
    public String getCategory() { return category; }
    public boolean isLearned() { return learned; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
