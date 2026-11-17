package com.example.linguaspace.vocabulary.importer;

import com.example.linguaspace.vocabulary.VocabularyInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Turns parsed table rows into vocabulary inputs, using the header row when there is one. */
public final class RowMapper {

    /** Column order when the data has no header row. */
    private static final List<String> DEFAULT_ORDER =
            List.of("word", "meaning", "pos", "example", "translation", "notes");

    private static final Map<String, String> HEADER_ALIASES = buildHeaderAliases();

    private RowMapper() {
    }

    /** @param error a problem with the row's shape, or null */
    public record MappedRow(int number, VocabularyInput input, String error) {
    }

    public static List<MappedRow> map(List<TableRow> table, String defaultLanguage, String defaultCategory) {
        if (table.isEmpty()) {
            return List.of();
        }
        Map<Integer, String> headerColumns = headerColumns(table.get(0));
        boolean hasHeader = headerColumns.containsValue("word");

        Map<Integer, String> columns;
        if (hasHeader) {
            columns = headerColumns;
        } else {
            columns = new HashMap<>();
            for (int i = 0; i < DEFAULT_ORDER.size(); i++) {
                columns.put(i, DEFAULT_ORDER.get(i));
            }
        }
        int columnLimit = hasHeader ? table.get(0).cells().size() : DEFAULT_ORDER.size();

        List<MappedRow> rows = new ArrayList<>();
        for (TableRow row : table.subList(hasHeader ? 1 : 0, table.size())) {
            rows.add(mapRow(row, columns, columnLimit, defaultLanguage, defaultCategory));
        }
        return rows;
    }

    private static MappedRow mapRow(
            TableRow row,
            Map<Integer, String> columns,
            int columnLimit,
            String defaultLanguage,
            String defaultCategory
    ) {
        Map<String, String> values = new HashMap<>();
        columns.forEach((index, field) -> {
            if (index < row.cells().size()) {
                values.put(field, row.cells().get(index));
            }
        });

        String error = row.error();
        for (int i = columnLimit; i < row.cells().size() && error == null; i++) {
            if (!row.cells().get(i).isBlank()) {
                error = "열이 너무 많습니다. 쉼표가 들어간 값은 큰따옴표로 감싸 주세요.";
            }
        }

        VocabularyInput input = new VocabularyInput(
                values.get("word"),
                values.get("meaning"),
                values.get("pos"),
                values.get("pronunciation"),
                values.get("example"),
                values.get("translation"),
                values.get("notes"),
                blankToDefault(values.get("language"), defaultLanguage),
                blankToDefault(values.get("category"), defaultCategory)
        );
        return new MappedRow(row.number(), input, error);
    }

    private static String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static Map<Integer, String> headerColumns(TableRow first) {
        Map<Integer, String> columns = new HashMap<>();
        for (int i = 0; i < first.cells().size(); i++) {
            String field = HEADER_ALIASES.get(headerKey(first.cells().get(i)));
            if (field != null) {
                columns.put(i, field);
            }
        }
        return columns;
    }

    private static String headerKey(String header) {
        return header.strip().toLowerCase(Locale.ROOT).replaceAll("[\\s_\\-]", "");
    }

    private static Map<String, String> buildHeaderAliases() {
        Map<String, String> aliases = new HashMap<>();
        put(aliases, "word", "word", "englishword", "english", "단어", "영어단어", "영단어", "표현");
        put(aliases, "meaning", "meaning", "koreanmeaning", "korean", "뜻", "의미", "한국어뜻");
        put(aliases, "pos", "partofspeech", "pos", "품사");
        put(aliases, "example", "example", "examplesentence", "sentence", "예문");
        put(aliases, "translation", "translation", "koreantranslation", "해석", "번역", "예문해석");
        put(aliases, "notes", "notes", "note", "memo", "메모", "비고");
        put(aliases, "pronunciation", "pronunciation", "발음");
        put(aliases, "language", "language", "lang", "언어");
        put(aliases, "category", "category", "tag", "카테고리", "분류");
        return Map.copyOf(aliases);
    }

    private static void put(Map<String, String> aliases, String field, String... names) {
        for (String name : names) {
            aliases.put(name, field);
        }
    }
}
