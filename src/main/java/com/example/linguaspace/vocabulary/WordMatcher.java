package com.example.linguaspace.vocabulary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Duplicate rule used everywhere: one entry per language + word + part of speech.
 * A word with no part of speech matches the same word with one, in either direction.
 */
final class WordMatcher {

    private WordMatcher() {
    }

    /** A saved entry or a not-yet-saved row, with the meanings it holds so far. */
    static final class Candidate {
        final VocabularyEntry saved;
        final List<VocabularyInput> pending = new ArrayList<>();
        private final Set<String> meaningKeys = new HashSet<>();
        private String partOfSpeech;
        private int meaningLength;

        Candidate(VocabularyEntry saved) {
            this.saved = saved;
            this.partOfSpeech = saved.getPartOfSpeech();
            addMeanings(saved.getMeaning());
        }

        Candidate(VocabularyInput first) {
            this.saved = null;
            this.partOfSpeech = first.partOfSpeech();
            addMeanings(first.meaning());
            pending.add(first);
        }

        boolean holdsAll(String meaning) {
            return VocabularyText.splitMeanings(meaning).stream()
                    .allMatch(item -> meaningKeys.contains(VocabularyText.key(item)));
        }

        /** Length of the meaning column if {@code meaning} were merged in. */
        int lengthAfterMerge(String meaning) {
            int length = meaningLength;
            for (String item : VocabularyText.splitMeanings(meaning)) {
                if (!meaningKeys.contains(VocabularyText.key(item))) {
                    length += item.length() + 2;
                }
            }
            return length;
        }

        void merge(VocabularyInput input) {
            addMeanings(input.meaning());
            if (partOfSpeech.isEmpty()) {
                partOfSpeech = input.partOfSpeech();
            }
            pending.add(input);
        }

        private void addMeanings(String meaning) {
            for (String item : VocabularyText.splitMeanings(meaning)) {
                if (meaningKeys.add(VocabularyText.key(item))) {
                    meaningLength += (meaningLength == 0 ? 0 : 2) + item.length();
                }
            }
        }
    }

    static Optional<Candidate> match(List<Candidate> candidates, VocabularyInput input) {
        String partOfSpeech = input.partOfSpeech();
        Optional<Candidate> exact = candidates.stream()
                .filter(candidate -> candidate.partOfSpeech.equals(partOfSpeech))
                .findFirst();
        if (exact.isPresent()) {
            return exact;
        }
        if (!partOfSpeech.isEmpty()) {
            return candidates.stream().filter(candidate -> candidate.partOfSpeech.isEmpty()).findFirst();
        }
        Optional<Candidate> sameMeaning = candidates.stream()
                .filter(candidate -> candidate.holdsAll(input.meaning()))
                .findFirst();
        if (sameMeaning.isPresent() || candidates.size() != 1) {
            return sameMeaning;
        }
        return Optional.of(candidates.get(0));
    }
}
