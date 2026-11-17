package com.example.linguaspace.vocabulary;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "vocabulary_entries",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vocabulary_language_word_pos",
                columnNames = {"language", "normalized_word", "part_of_speech"}
        ),
        indexes = @Index(name = "idx_vocabulary_created_at", columnList = "created_at")
)
public class VocabularyEntry {

    // A sequence (not IDENTITY) lets Hibernate batch inserts, which keeps bulk imports fast on a remote database.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vocabulary_entries_seq")
    @SequenceGenerator(name = "vocabulary_entries_seq", sequenceName = "vocabulary_entries_seq", allocationSize = 100)
    private Long id;

    @Version
    private long version;

    @Column(nullable = false, length = 10)
    private String language;

    @Column(nullable = false, length = VocabularyInput.MAX_WORD)
    private String word;

    @Column(name = "normalized_word", nullable = false, length = VocabularyInput.MAX_WORD)
    private String normalizedWord;

    @Column(nullable = false, length = VocabularyInput.MAX_MEANING)
    private String meaning;

    @Column(name = "part_of_speech", nullable = false, length = VocabularyInput.MAX_POS)
    private String partOfSpeech;

    @Column(length = VocabularyInput.MAX_PRONUNCIATION)
    private String pronunciation;

    @Column(length = VocabularyInput.MAX_SENTENCE)
    private String example;

    @Column(length = VocabularyInput.MAX_SENTENCE)
    private String translation;

    @Column(length = VocabularyInput.MAX_NOTES)
    private String notes;

    @Column(length = VocabularyInput.MAX_CATEGORY)
    private String category;

    @Column(nullable = false)
    private boolean learned;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected VocabularyEntry() {
    }

    public VocabularyEntry(VocabularyInput input) {
        apply(input);
    }

    /** Replaces every editable field. */
    public final void apply(VocabularyInput input) {
        language = input.language();
        word = input.word();
        normalizedWord = VocabularyText.key(input.word());
        meaning = input.meaning();
        partOfSpeech = input.partOfSpeech();
        pronunciation = input.pronunciation();
        example = input.example();
        translation = input.translation();
        notes = input.notes();
        category = input.category();
    }

    /** Adds new meanings and fills empty fields only. Existing content is never overwritten. */
    public void mergeFrom(VocabularyInput input) {
        List<String> merged = new ArrayList<>(VocabularyText.splitMeanings(meaning));
        merged.addAll(VocabularyText.splitMeanings(input.meaning()));
        meaning = VocabularyText.joinMeanings(VocabularyText.splitMeanings(String.join("\n", merged)));
        partOfSpeech = firstNonEmpty(partOfSpeech, input.partOfSpeech());
        pronunciation = firstNonEmpty(pronunciation, input.pronunciation());
        example = firstNonEmpty(example, input.example());
        translation = firstNonEmpty(translation, input.translation());
        notes = firstNonEmpty(notes, input.notes());
        category = firstNonEmpty(category, input.category());
    }

    private static String firstNonEmpty(String current, String incoming) {
        return current == null || current.isEmpty() ? incoming : current;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setLearned(boolean learned) {
        this.learned = learned;
    }

    public Long getId() { return id; }
    public String getLanguage() { return language; }
    public String getWord() { return word; }
    public String getNormalizedWord() { return normalizedWord; }
    public String getMeaning() { return meaning; }
    public String getPartOfSpeech() { return partOfSpeech; }
    public String getPronunciation() { return pronunciation; }
    public String getExample() { return example; }
    public String getTranslation() { return translation; }
    public String getNotes() { return notes; }
    public String getCategory() { return category; }
    public boolean isLearned() { return learned; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
