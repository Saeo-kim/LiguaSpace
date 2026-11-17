package com.example.linguaspace.vocabulary;

import java.util.ArrayList;
import java.util.List;

/** One word as submitted by the form or by an import row. */
public record VocabularyInput(
        String word,
        String meaning,
        String partOfSpeech,
        String pronunciation,
        String example,
        String translation,
        String notes,
        String language,
        String category
) {

    static final int MAX_WORD = 200;
    static final int MAX_MEANING = 1000;
    static final int MAX_POS = 40;
    static final int MAX_PRONUNCIATION = 200;
    static final int MAX_SENTENCE = 2000;
    static final int MAX_NOTES = 5000;
    static final int MAX_CATEGORY = 100;

    public VocabularyInput cleaned() {
        String rawLanguage = VocabularyText.singleLine(language);
        return new VocabularyInput(
                VocabularyText.singleLine(word),
                VocabularyText.joinMeanings(VocabularyText.splitMeanings(meaning)),
                VocabularyText.canonicalPartOfSpeech(partOfSpeech),
                VocabularyText.singleLine(pronunciation),
                VocabularyText.clean(example),
                VocabularyText.clean(translation),
                VocabularyText.clean(notes),
                VocabularyLanguages.resolve(rawLanguage).orElse(rawLanguage),
                VocabularyText.singleLine(category)
        );
    }

    /** Expects a {@link #cleaned()} instance. Returns human-readable problems, empty when valid. */
    public List<String> problems() {
        List<String> problems = new ArrayList<>();
        if (word.isEmpty()) {
            problems.add("단어가 비어 있습니다.");
        }
        if (meaning.isEmpty()) {
            problems.add("뜻이 비어 있습니다.");
        }
        if (VocabularyLanguages.resolve(language).isEmpty()) {
            problems.add("지원하지 않는 언어입니다: " + (language.isEmpty() ? "(없음)" : language));
        }
        checkLength(problems, "단어", word, MAX_WORD);
        checkLength(problems, "뜻", meaning, MAX_MEANING);
        checkLength(problems, "품사", partOfSpeech, MAX_POS);
        checkLength(problems, "발음", pronunciation, MAX_PRONUNCIATION);
        checkLength(problems, "예문", example, MAX_SENTENCE);
        checkLength(problems, "해석", translation, MAX_SENTENCE);
        checkLength(problems, "메모", notes, MAX_NOTES);
        checkLength(problems, "카테고리", category, MAX_CATEGORY);
        return problems;
    }

    private static void checkLength(List<String> problems, String label, String value, int max) {
        if (value.length() > max) {
            problems.add(label + "이(가) 너무 깁니다 (최대 " + max + "자).");
        }
    }
}
