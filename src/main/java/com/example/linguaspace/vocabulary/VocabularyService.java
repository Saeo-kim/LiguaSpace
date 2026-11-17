package com.example.linguaspace.vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VocabularyService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Sort NEWEST_FIRST = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

    private final VocabularyRepository repository;

    public VocabularyService(VocabularyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<VocabularyView> search(String query, String language, int page, int size) {
        String trimmed = VocabularyText.key(query);
        String pattern = trimmed.isEmpty() ? "" : "%" + escapeLike(trimmed) + "%";
        String languageFilter = VocabularyLanguages.resolve(language).orElse("");
        PageRequest request = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE), NEWEST_FIRST);
        return repository.search(languageFilter, pattern, request).map(VocabularyView::from);
    }

    /** @param merge when true, new meanings are added to an existing word instead of reporting a conflict */
    @Transactional
    public VocabularyView create(VocabularyInput rawInput, boolean merge) {
        VocabularyInput input = validated(rawInput);
        Optional<WordMatcher.Candidate> match = WordMatcher.match(savedCandidates(input), input);
        if (match.isEmpty()) {
            return VocabularyView.from(repository.save(new VocabularyEntry(input)));
        }

        WordMatcher.Candidate candidate = match.get();
        VocabularyView existing = VocabularyView.from(candidate.saved);
        if (candidate.holdsAll(input.meaning())) {
            throw new VocabularyException(HttpStatus.CONFLICT, "DUPLICATE", "이미 저장된 단어입니다.", existing);
        }
        if (!merge) {
            throw new VocabularyException(
                    HttpStatus.CONFLICT, "MERGEABLE", "이미 있는 단어입니다. 새 뜻을 추가할까요?", existing);
        }
        if (candidate.lengthAfterMerge(input.meaning()) > VocabularyInput.MAX_MEANING) {
            throw new VocabularyException(HttpStatus.BAD_REQUEST, "뜻을 합치면 최대 길이를 넘습니다.");
        }
        candidate.saved.mergeFrom(input);
        return VocabularyView.from(candidate.saved);
    }

    @Transactional
    public VocabularyView update(long id, VocabularyInput rawInput) {
        VocabularyInput input = validated(rawInput);
        VocabularyEntry entry = find(id);
        Optional<VocabularyEntry> conflict = repository
                .findByLanguageAndNormalizedWordIn(input.language(), Set.of(VocabularyText.key(input.word())))
                .stream()
                .filter(other -> !other.getId().equals(id))
                .filter(other -> other.getPartOfSpeech().equals(input.partOfSpeech()))
                .findFirst();
        if (conflict.isPresent()) {
            throw new VocabularyException(HttpStatus.CONFLICT, "DUPLICATE",
                    "같은 단어와 품사가 이미 있습니다.", VocabularyView.from(conflict.get()));
        }
        entry.apply(input);
        return VocabularyView.from(entry);
    }

    @Transactional
    public VocabularyView setLearned(long id, boolean learned) {
        VocabularyEntry entry = find(id);
        entry.setLearned(learned);
        return VocabularyView.from(entry);
    }

    @Transactional
    public void delete(long id) {
        repository.delete(find(id));
    }

    private VocabularyEntry find(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new VocabularyException(HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다."));
    }

    private List<WordMatcher.Candidate> savedCandidates(VocabularyInput input) {
        List<WordMatcher.Candidate> candidates = new ArrayList<>();
        repository.findByLanguageAndNormalizedWordIn(input.language(), Set.of(VocabularyText.key(input.word())))
                .forEach(entry -> candidates.add(new WordMatcher.Candidate(entry)));
        return candidates;
    }

    private static VocabularyInput validated(VocabularyInput rawInput) {
        VocabularyInput input = rawInput.cleaned();
        List<String> problems = input.problems();
        if (!problems.isEmpty()) {
            throw new VocabularyException(HttpStatus.BAD_REQUEST, String.join(" ", problems));
        }
        return input;
    }

    private static String escapeLike(String value) {
        return value.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
