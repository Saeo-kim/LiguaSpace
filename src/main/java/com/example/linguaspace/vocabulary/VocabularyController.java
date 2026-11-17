package com.example.linguaspace.vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {

    private final VocabularyService service;
    private final VocabularyImportService importService;

    public VocabularyController(VocabularyService service, VocabularyImportService importService) {
        this.service = service;
        this.importService = importService;
    }

    public record PageResponse(List<VocabularyView> items, long total, boolean hasMore) {
    }

    public record LearnedRequest(boolean learned) {
    }

    public record CommitRequest(List<VocabularyImportService.ImportRow> rows) {
    }

    @GetMapping
    public PageResponse search(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Page<VocabularyView> result = service.search(q, language, page, size);
        return new PageResponse(result.getContent(), result.getTotalElements(), result.hasNext());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VocabularyView create(
            @RequestBody VocabularyInput input,
            @RequestParam(defaultValue = "false") boolean merge
    ) {
        return service.create(input, merge);
    }

    @PutMapping("/{id}")
    public VocabularyView update(@PathVariable long id, @RequestBody VocabularyInput input) {
        return service.update(id, input);
    }

    @PatchMapping("/{id}/learned")
    public VocabularyView setLearned(@PathVariable long id, @RequestBody LearnedRequest request) {
        return service.setLearned(id, request.learned());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

    @PostMapping("/import/preview")
    public VocabularyImportService.Preview preview(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "en") String language,
            @RequestParam(defaultValue = "") String category
    ) throws IOException {
        byte[] bytes = file == null ? null : file.getBytes();
        String name = file == null ? null : file.getOriginalFilename();
        return importService.preview(bytes, name, text, language, category);
    }

    @PostMapping("/import/commit")
    public VocabularyImportService.CommitResult commit(@RequestBody CommitRequest request) {
        return importService.commit(request.rows());
    }
}
