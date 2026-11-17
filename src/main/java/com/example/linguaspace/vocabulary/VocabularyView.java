package com.example.linguaspace.vocabulary;

import java.time.LocalDateTime;

public record VocabularyView(
        Long id,
        String word,
        String meaning,
        String partOfSpeech,
        String pronunciation,
        String example,
        String translation,
        String notes,
        String language,
        String category,
        boolean learned,
        LocalDateTime createdAt
) {

    static VocabularyView from(VocabularyEntry entry) {
        return new VocabularyView(
                entry.getId(), entry.getWord(), entry.getMeaning(), entry.getPartOfSpeech(),
                entry.getPronunciation(), entry.getExample(), entry.getTranslation(), entry.getNotes(),
                entry.getLanguage(), entry.getCategory(), entry.isLearned(), entry.getCreatedAt()
        );
    }
}
