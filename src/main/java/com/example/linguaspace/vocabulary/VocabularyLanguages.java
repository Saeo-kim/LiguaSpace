package com.example.linguaspace.vocabulary;

import java.util.Map;
import java.util.Optional;

/** The language spaces of the vocabulary page. Accepts codes and common English/Korean names. */
public final class VocabularyLanguages {

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("en", "en"), Map.entry("english", "en"), Map.entry("영어", "en"),
            Map.entry("zh", "zh"), Map.entry("chinese", "zh"), Map.entry("중국어", "zh"), Map.entry("中文", "zh"),
            Map.entry("ja", "ja"), Map.entry("japanese", "ja"), Map.entry("일본어", "ja"), Map.entry("日本語", "ja"),
            Map.entry("es", "es"), Map.entry("spanish", "es"), Map.entry("스페인어", "es"), Map.entry("español", "es"),
            Map.entry("ko", "ko"), Map.entry("korean", "ko"), Map.entry("한국어", "ko")
    );

    private VocabularyLanguages() {
    }

    public static Optional<String> resolve(String value) {
        return Optional.ofNullable(ALIASES.get(VocabularyText.key(value)));
    }
}
