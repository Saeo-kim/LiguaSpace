package com.example.linguaspace.vocabulary;

import com.example.linguaspace.vocabulary.importer.DelimitedTextParser;
import com.example.linguaspace.vocabulary.importer.ImportFileException;
import com.example.linguaspace.vocabulary.importer.RowMapper;
import com.example.linguaspace.vocabulary.importer.TableRow;
import com.example.linguaspace.vocabulary.importer.TextDecoder;
import com.example.linguaspace.vocabulary.importer.XlsxReader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Bulk import in two steps. Preview never writes. Commit plans again from scratch against the
 * current database, so re-submitting the same rows cannot create duplicates, and it saves in a
 * single transaction: an unexpected database failure leaves nothing half-imported.
 */
@Service
public class VocabularyImportService {

    public static final int MAX_ROWS = 5000;
    private static final int LOOKUP_CHUNK = 500;
    private static final String TOO_MANY_ROWS = "한 번에 최대 " + MAX_ROWS + "행까지 가져올 수 있습니다.";

    public enum RowStatus { NEW, MERGE, DUPLICATE, INVALID }

    public record ImportRow(
            int rowNumber,
            String word,
            String meaning,
            String partOfSpeech,
            String pronunciation,
            String example,
            String translation,
            String notes,
            String language,
            String category,
            RowStatus status,
            String message
    ) {
        VocabularyInput toInput() {
            return new VocabularyInput(
                    word, meaning, partOfSpeech, pronunciation, example, translation, notes, language, category);
        }
    }

    public record Preview(List<ImportRow> rows, int newCount, int mergeCount, int duplicateCount, int invalidCount) {
    }

    public record Failure(int rowNumber, String word, String message) {
    }

    public record CommitResult(int created, int merged, int skipped, int failed, List<Failure> failures) {
    }

    /** Every row's outcome, plus the candidates to persist. */
    private record Plan(List<ImportRow> rows, List<WordMatcher.Candidate> changed) {
    }

    private final VocabularyRepository repository;

    public VocabularyImportService(VocabularyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Preview preview(byte[] file, String fileName, String text, String language, String category) {
        List<TableRow> table = file != null && file.length > 0 ? parseFile(file, fileName) : parseText(text);
        List<RowMapper.MappedRow> mapped = RowMapper.map(table, language, category);
        if (mapped.isEmpty()) {
            throw new VocabularyException(HttpStatus.BAD_REQUEST, "가져올 데이터가 없습니다.");
        }
        if (mapped.size() > MAX_ROWS) {
            throw new VocabularyException(HttpStatus.BAD_REQUEST, TOO_MANY_ROWS);
        }
        return toPreview(plan(mapped));
    }

    @Transactional
    public CommitResult commit(List<ImportRow> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new VocabularyException(HttpStatus.BAD_REQUEST, "저장할 행이 없습니다.");
        }
        if (rows.size() > MAX_ROWS) {
            throw new VocabularyException(HttpStatus.BAD_REQUEST, TOO_MANY_ROWS);
        }

        List<RowMapper.MappedRow> mapped = new ArrayList<>();
        for (ImportRow row : rows) {
            // A row the preview rejected stays rejected, even if its text alone would pass validation.
            String error = row.status() == RowStatus.INVALID ? row.message() : null;
            mapped.add(new RowMapper.MappedRow(row.rowNumber(), row.toInput(), error));
        }
        Plan plan = plan(mapped);

        List<VocabularyEntry> newEntries = new ArrayList<>();
        for (WordMatcher.Candidate candidate : plan.changed()) {
            if (candidate.saved != null) {
                candidate.pending.forEach(candidate.saved::mergeFrom);
            } else {
                VocabularyEntry entry = new VocabularyEntry(candidate.pending.get(0));
                candidate.pending.stream().skip(1).forEach(entry::mergeFrom);
                newEntries.add(entry);
            }
        }
        repository.saveAll(newEntries);

        Preview counts = toPreview(plan);
        List<Failure> failures = plan.rows().stream()
                .filter(row -> row.status() == RowStatus.INVALID)
                .map(row -> new Failure(row.rowNumber(), row.word(), row.message()))
                .toList();
        return new CommitResult(
                counts.newCount(), counts.mergeCount(), counts.duplicateCount(), counts.invalidCount(), failures);
    }

    private Plan plan(List<RowMapper.MappedRow> mapped) {
        List<VocabularyInput> cleaned = mapped.stream().map(row -> row.input().cleaned()).toList();
        Map<String, List<WordMatcher.Candidate>> candidates = loadSavedCandidates(mapped, cleaned);

        Set<WordMatcher.Candidate> changed = new LinkedHashSet<>();
        List<ImportRow> rows = new ArrayList<>(mapped.size());
        for (int i = 0; i < mapped.size(); i++) {
            RowMapper.MappedRow row = mapped.get(i);
            VocabularyInput input = cleaned.get(i);

            List<String> problems = row.error() != null ? List.of(row.error()) : input.problems();
            if (!problems.isEmpty()) {
                rows.add(toRow(row.number(), input, RowStatus.INVALID, String.join(" ", problems)));
                continue;
            }

            List<WordMatcher.Candidate> sameWord =
                    candidates.computeIfAbsent(wordKey(input), key -> new ArrayList<>());
            Optional<WordMatcher.Candidate> match = WordMatcher.match(sameWord, input);
            if (match.isEmpty()) {
                WordMatcher.Candidate created = new WordMatcher.Candidate(input);
                sameWord.add(created);
                changed.add(created);
                rows.add(toRow(row.number(), input, RowStatus.NEW, ""));
                continue;
            }

            WordMatcher.Candidate candidate = match.get();
            if (candidate.holdsAll(input.meaning())) {
                String message = candidate.saved != null ? "이미 저장된 단어입니다." : "파일 안에서 중복된 행입니다.";
                rows.add(toRow(row.number(), input, RowStatus.DUPLICATE, message));
            } else if (candidate.lengthAfterMerge(input.meaning()) > VocabularyInput.MAX_MEANING) {
                rows.add(toRow(row.number(), input, RowStatus.INVALID, "뜻을 합치면 최대 길이를 넘습니다."));
            } else {
                candidate.merge(input);
                changed.add(candidate);
                rows.add(toRow(row.number(), input, RowStatus.MERGE, "기존 단어에 새 뜻을 추가합니다."));
            }
        }
        return new Plan(rows, List.copyOf(changed));
    }

    private Map<String, List<WordMatcher.Candidate>> loadSavedCandidates(
            List<RowMapper.MappedRow> mapped,
            List<VocabularyInput> cleaned
    ) {
        Map<String, Set<String>> wordsByLanguage = new HashMap<>();
        for (int i = 0; i < mapped.size(); i++) {
            VocabularyInput input = cleaned.get(i);
            if (mapped.get(i).error() == null && input.problems().isEmpty()) {
                wordsByLanguage.computeIfAbsent(input.language(), key -> new TreeSet<>())
                        .add(VocabularyText.key(input.word()));
            }
        }

        Map<String, List<WordMatcher.Candidate>> candidates = new HashMap<>();
        wordsByLanguage.forEach((language, words) -> {
            List<String> all = List.copyOf(words);
            for (int start = 0; start < all.size(); start += LOOKUP_CHUNK) {
                List<String> chunk = all.subList(start, Math.min(start + LOOKUP_CHUNK, all.size()));
                for (VocabularyEntry entry : repository.findByLanguageAndNormalizedWordIn(language, chunk)) {
                    candidates.computeIfAbsent(language + '|' + entry.getNormalizedWord(), key -> new ArrayList<>())
                            .add(new WordMatcher.Candidate(entry));
                }
            }
        });
        return candidates;
    }

    private static String wordKey(VocabularyInput input) {
        return input.language() + '|' + VocabularyText.key(input.word());
    }

    private static ImportRow toRow(int number, VocabularyInput input, RowStatus status, String message) {
        return new ImportRow(
                number, input.word(), input.meaning(), input.partOfSpeech(), input.pronunciation(), input.example(),
                input.translation(), input.notes(), input.language(), input.category(), status, message
        );
    }

    private static Preview toPreview(Plan plan) {
        Map<RowStatus, Integer> counts = new HashMap<>();
        plan.rows().forEach(row -> counts.merge(row.status(), 1, Integer::sum));
        return new Preview(
                plan.rows(),
                counts.getOrDefault(RowStatus.NEW, 0),
                counts.getOrDefault(RowStatus.MERGE, 0),
                counts.getOrDefault(RowStatus.DUPLICATE, 0),
                counts.getOrDefault(RowStatus.INVALID, 0)
        );
    }

    private static List<TableRow> parseText(String text) {
        if (text == null || text.isBlank()) {
            throw new VocabularyException(HttpStatus.BAD_REQUEST, "파일을 선택하거나 내용을 붙여 넣어 주세요.");
        }
        return DelimitedTextParser.parse(text.startsWith("﻿") ? text.substring(1) : text);
    }

    private static List<TableRow> parseFile(byte[] file, String fileName) {
        String name = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
        if (name.endsWith(".xls")) {
            throw new ImportFileException("예전 .xls 형식은 지원하지 않습니다. .xlsx 또는 CSV로 저장해 주세요.");
        }
        if (name.endsWith(".xlsx")) {
            return XlsxReader.read(file, MAX_ROWS + 2);
        }
        return DelimitedTextParser.parse(TextDecoder.decode(file));
    }
}
