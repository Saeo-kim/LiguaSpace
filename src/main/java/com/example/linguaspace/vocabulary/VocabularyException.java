package com.example.linguaspace.vocabulary;

import org.springframework.http.HttpStatus;

/** An expected failure whose message is safe to show to the user. */
public class VocabularyException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final transient VocabularyView existing;

    public VocabularyException(HttpStatus status, String code, String message, VocabularyView existing) {
        super(message);
        this.status = status;
        this.code = code;
        this.existing = existing;
    }

    public VocabularyException(HttpStatus status, String message) {
        this(status, "ERROR", message, null);
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public VocabularyView getExisting() { return existing; }
}
