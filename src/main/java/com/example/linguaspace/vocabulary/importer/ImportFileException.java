package com.example.linguaspace.vocabulary.importer;

/** The uploaded file as a whole cannot be read. The message is safe to show to the user. */
public class ImportFileException extends RuntimeException {

    public ImportFileException(String message) {
        super(message);
    }
}
