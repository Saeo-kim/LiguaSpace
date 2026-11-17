package com.example.linguaspace.vocabulary.importer;

import java.util.List;

/**
 * One row of a parsed file.
 *
 * @param number the row or line number as the user sees it in their file
 * @param error  a parsing problem for this row, or null
 */
public record TableRow(int number, List<String> cells, String error) {

    boolean isBlank() {
        return error == null && cells.stream().allMatch(String::isBlank);
    }
}
