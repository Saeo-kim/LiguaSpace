package com.example.linguaspace.vocabulary;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Text clean-up and normalisation rules shared by manual entry and bulk import. */
public final class VocabularyText {

    private static final Map<String, String> PARTS_OF_SPEECH = buildPartsOfSpeech();

    private VocabularyText() {
    }

    /** Removes characters PostgreSQL cannot store (NUL, lone surrogates) and other control characters. */
    public static String clean(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFC)
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[\\p{Cc}\\p{Cs}&&[^\\n\\t]]", "")
                .strip();
    }

    public static String singleLine(String value) {
        return clean(value).replaceAll("\\s+", " ");
    }

    public static String key(String value) {
        return singleLine(value).toLowerCase(Locale.ROOT);
    }

    /** Multiple meanings are separated by ";", "；" or a line break. Repeats are dropped. */
    public static List<String> splitMeanings(String value) {
        Set<String> seen = new HashSet<>();
        List<String> meanings = new ArrayList<>();
        for (String part : clean(value).split("[;；\\n]+")) {
            String meaning = singleLine(part);
            if (!meaning.isEmpty() && seen.add(key(meaning))) {
                meanings.add(meaning);
            }
        }
        return meanings;
    }

    public static String joinMeanings(List<String> meanings) {
        return String.join("; ", meanings);
    }

    public static String canonicalPartOfSpeech(String value) {
        String lower = key(value);
        return PARTS_OF_SPEECH.getOrDefault(lower, lower);
    }

    private static Map<String, String> buildPartsOfSpeech() {
        Map<String, String> map = new HashMap<>();
        addAll(map, "verb", "v", "v.", "vt", "vt.", "vi", "vi.", "동사", "동");
        addAll(map, "noun", "n", "n.", "명사", "명");
        addAll(map, "adjective", "adj", "adj.", "a.", "형용사", "형");
        addAll(map, "adverb", "adv", "adv.", "부사", "부");
        addAll(map, "preposition", "prep", "prep.", "전치사");
        addAll(map, "conjunction", "conj", "conj.", "접속사");
        addAll(map, "pronoun", "pron", "pron.", "대명사");
        addAll(map, "interjection", "int", "int.", "interj", "interj.", "감탄사");
        addAll(map, "phrase", "phr", "phr.", "구", "숙어");
        return Map.copyOf(map);
    }

    private static void addAll(Map<String, String> map, String canonical, String... aliases) {
        for (String alias : aliases) {
            map.put(alias, canonical);
        }
    }
}
