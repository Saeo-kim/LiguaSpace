package com.example.linguaspace.korean;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/korean-entries")
public class KoreanEntryController {

    private final KoreanEntryRepository repository;

    public KoreanEntryController(KoreanEntryRepository repository) {
        this.repository = repository;
    }

    // 모든 기록 조회
    @GetMapping
    public List<KoreanEntry> getAllEntries() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    // 새 기록 저장
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KoreanEntry createEntry(@RequestBody KoreanEntryRequest request) {

        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "기록 제목은 필수입니다."
            );
        }

        KoreanEntry entry = new KoreanEntry(
                request.title().trim(),
                request.type(),
                request.source(),
                request.note(),
                request.question()
        );

        return repository.save(entry);
    }

    // 기록 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "기록을 찾을 수 없습니다."
            );
        }

        repository.deleteById(id);
    }

    public record KoreanEntryRequest(
            String title,
            String type,
            String source,
            String note,
            String question
    ) {
    }
}