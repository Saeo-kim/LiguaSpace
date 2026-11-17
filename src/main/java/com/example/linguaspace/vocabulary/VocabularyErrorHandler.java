package com.example.linguaspace.vocabulary;

import com.example.linguaspace.vocabulary.importer.ImportFileException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@RestControllerAdvice
public class VocabularyErrorHandler {

    public record ErrorBody(String code, String message, VocabularyView existing) {
    }

    @ExceptionHandler(VocabularyException.class)
    ResponseEntity<ErrorBody> expected(VocabularyException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new ErrorBody(e.getCode(), e.getMessage(), e.getExisting()));
    }

    @ExceptionHandler(ImportFileException.class)
    ResponseEntity<Map<String, String>> badFile(ImportFileException e) {
        return ResponseEntity.badRequest().body(Map.of("code", "BAD_FILE", "message", e.getMessage()));
    }

    /** Two requests saved the same word at once. The transaction rolled back, so a retry is safe. */
    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class})
    ResponseEntity<Map<String, String>> concurrentChange(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "code", "CONCURRENT_CHANGE",
                "message", "동시에 변경되어 저장하지 못했습니다. 아무것도 저장되지 않았으니 다시 시도해 주세요."
        ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<Map<String, String>> tooLarge(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(Map.of(
                "code", "TOO_LARGE",
                "message", "파일은 5MB까지 올릴 수 있습니다."
        ));
    }
}
