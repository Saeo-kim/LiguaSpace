package com.example.linguaspace.vocabulary.importer;

import java.util.ArrayList;
import java.util.List;

/** RFC 4180 parser for CSV and for tab-separated text pasted from Excel or Google Sheets. */
public final class DelimitedTextParser {

    private DelimitedTextParser() {
    }

    public static List<TableRow> parse(String text) {
        char delimiter = detectDelimiter(text);
        List<TableRow> rows = new ArrayList<>();
        List<String> cells = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean inQuotes = false;
        boolean atCellStart = true;
        int line = 1;
        int rowStartLine = 1;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inQuotes) {
                if (c == '"' && i + 1 < text.length() && text.charAt(i + 1) == '"') {
                    cell.append('"');
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    if (c == '\n') {
                        line++;
                    }
                    cell.append(c);
                }
            } else if (c == '"' && atCellStart) {
                inQuotes = true;
                atCellStart = false;
            } else if (c == delimiter) {
                cells.add(cell.toString());
                cell.setLength(0);
                atCellStart = true;
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    i++;
                }
                cells.add(cell.toString());
                cell.setLength(0);
                addIfNotBlank(rows, new TableRow(rowStartLine, cells, null));
                cells = new ArrayList<>();
                atCellStart = true;
                line++;
                rowStartLine = line;
            } else {
                cell.append(c);
                atCellStart = false;
            }
        }

        cells.add(cell.toString());
        String error = inQuotes ? "닫히지 않은 따옴표(\")가 있습니다." : null;
        addIfNotBlank(rows, new TableRow(rowStartLine, cells, error));
        return rows;
    }

    private static void addIfNotBlank(List<TableRow> rows, TableRow row) {
        if (!row.isBlank()) {
            rows.add(row);
        }
    }

    private static char detectDelimiter(String text) {
        for (String line : text.split("\\R")) {
            if (!line.isBlank()) {
                return line.indexOf('\t') >= 0 ? '\t' : ',';
            }
        }
        return ',';
    }
}
