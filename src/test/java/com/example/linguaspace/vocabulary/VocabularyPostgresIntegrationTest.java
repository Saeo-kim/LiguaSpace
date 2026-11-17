package com.example.linguaspace.vocabulary;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Runs against a real PostgreSQL server with the production schema setting (ddl-auto=update). */
@SpringBootTest
@AutoConfigureMockMvc
class VocabularyPostgresIntegrationTest {

    private static final EmbeddedPostgres POSTGRES = start();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DataSource dataSource;

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }

    private static EmbeddedPostgres start() {
        try {
            return EmbeddedPostgres.builder().start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @BeforeEach
    void emptyTable() throws SQLException {
        execute("DELETE FROM vocabulary_entries");
    }

    @Test
    void existingPagesAndKoreanArchiveStillWork() throws Exception {
        for (String page : List.of("/", "/vocabulary", "/korean", "/study-requests")) {
            mvc.perform(get(page)).andExpect(status().isOk());
        }
        mvc.perform(get("/vocabulary.js")).andExpect(status().isOk());
        mvc.perform(get("/manifest.json")).andExpect(status().isOk());
        mvc.perform(get("/healthz")).andExpect(status().isOk());
        mvc.perform(get("/healthz/ready")).andExpect(status().isOk());

        String created = mvc.perform(post("/api/korean-entries").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"여운\",\"type\":\"어휘\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String id = created.replaceAll(".*\"id\":(\\d+).*", "$1");
        mvc.perform(get("/api/korean-entries")).andExpect(content().string(containsString("여운")));
        mvc.perform(delete("/api/korean-entries/" + id)).andExpect(status().isNoContent());

        mvc.perform(post("/study-requests")
                        .param("language", "ko").param("goal", "말하기").param("topic", "회화"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void createsEditsSearchesAndDeletes() throws Exception {
        String id = idOf(createWord("accomplish", "성취하다", "v").andExpect(status().isCreated()));

        mvc.perform(get("/api/vocabulary").param("q", "성취"))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].word").value("accomplish"))
                .andExpect(jsonPath("$.items[0].partOfSpeech").value("verb"))
                .andExpect(jsonPath("$.items[0].createdAt").exists());
        mvc.perform(get("/api/vocabulary").param("q", "ACCOMP").param("language", "en"))
                .andExpect(jsonPath("$.total").value(1));
        mvc.perform(get("/api/vocabulary").param("q", "accomplish").param("language", "ja"))
                .andExpect(jsonPath("$.total").value(0));
        mvc.perform(get("/api/vocabulary").param("q", "%"))
                .andExpect(jsonPath("$.total").value(0));

        mvc.perform(put("/api/vocabulary/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(word("achieve", "달성하다", "verb")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("achieve"));
        mvc.perform(patch("/api/vocabulary/" + id + "/learned").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"learned\":true}"))
                .andExpect(jsonPath("$.learned").value(true));
        mvc.perform(delete("/api/vocabulary/" + id)).andExpect(status().isNoContent());
        assertThat(count()).isZero();
    }

    @Test
    void rejectsDuplicatesAndMergesNewMeanings() throws Exception {
        createWord("run", "달리다", "verb").andExpect(status().isCreated());

        createWord(" RUN ", "달리다", "v")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE"));
        createWord("run", "운영하다", "verb")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("MERGEABLE"));
        assertThat(count()).isEqualTo(1);

        mvc.perform(post("/api/vocabulary").param("merge", "true").contentType(MediaType.APPLICATION_JSON)
                        .content(word("run", "운영하다", "verb")))
                .andExpect(jsonPath("$.meaning").value("달리다; 운영하다"));

        createWord("run", "달리기", "noun").andExpect(status().isCreated());
        assertThat(count()).isEqualTo(2);
    }

    @Test
    void refusesEditThatCollidesWithAnotherEntry() throws Exception {
        createWord("run", "달리다", "verb");
        String id = idOf(createWord("walk", "걷다", "verb"));
        mvc.perform(put("/api/vocabulary/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(word("run", "걷다", "verb")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE"));
    }

    @Test
    void validatesRequiredFields() throws Exception {
        createWord("", "뜻", "").andExpect(status().isBadRequest());
        createWord("word", "   ", "").andExpect(status().isBadRequest());
    }

    @Test
    void storesTextContainingNulCharacters() throws Exception {
        createWord("nul\\u0000word", "뜻\\u0000", "").andExpect(status().isCreated());
        mvc.perform(get("/api/vocabulary")).andExpect(jsonPath("$.items[0].word").value("nulword"));
    }

    @Test
    void previewClassifiesRowsAndNeverWrites() throws Exception {
        createWord("run", "달리다", "verb");

        String csv = """
                Word,Meaning,PartOfSpeech,Example,Translation,Notes
                accomplish,성취하다,verb,I accomplished my goal.,나는 목표를 달성했다.,
                run,달리다,verb,,,
                run,운영하다,verb,,,
                ,뜻만 있음,,,,
                nomeaning,,,,,
                book,책,noun,,,
                book,책,noun,,,
                """;

        preview(csv)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newCount").value(2))
                .andExpect(jsonPath("$.mergeCount").value(1))
                .andExpect(jsonPath("$.duplicateCount").value(2))
                .andExpect(jsonPath("$.invalidCount").value(2))
                .andExpect(jsonPath("$.rows[3].rowNumber").value(5))
                .andExpect(jsonPath("$.rows[3].status").value("INVALID"));
        assertThat(count()).isEqualTo(1);
    }

    @Test
    void commitReportsCountsAndRepeatingItAddsNothing() throws Exception {
        createWord("run", "달리다", "verb");
        String previewJson = previewBody("""
                word,meaning,pos
                accomplish,성취하다,v
                run,운영하다,v
                nomeaning,,
                """);

        commit(previewJson)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(1))
                .andExpect(jsonPath("$.merged").value(1))
                .andExpect(jsonPath("$.skipped").value(0))
                .andExpect(jsonPath("$.failed").value(1))
                .andExpect(jsonPath("$.failures[0].rowNumber").value(4));
        assertThat(count()).isEqualTo(2);

        commit(previewJson)
                .andExpect(jsonPath("$.created").value(0))
                .andExpect(jsonPath("$.merged").value(0))
                .andExpect(jsonPath("$.skipped").value(2));
        assertThat(count()).isEqualTo(2);
        mvc.perform(get("/api/vocabulary").param("q", "run"))
                .andExpect(jsonPath("$.items[0].meaning").value("달리다; 운영하다"));
    }

    @Test
    void rowRejectedInPreviewStaysRejectedOnCommit() throws Exception {
        String previewJson = previewBody("good,좋은\nrun,달리다,verb,I ran, then stopped.,해석,메모,초과");
        commit(previewJson)
                .andExpect(jsonPath("$.created").value(1))
                .andExpect(jsonPath("$.failed").value(1));
        assertThat(count()).isEqualTo(1);
    }

    @Test
    void uploadsFilesThroughThePreviewEndpoint() throws Exception {
        MockMultipartFile csv = new MockMultipartFile("file", "words.csv", "text/csv",
                "﻿Word,Meaning\nbook,책\n".getBytes(StandardCharsets.UTF_8));
        mvc.perform(multipart("/api/vocabulary/import/preview").file(csv))
                .andExpect(jsonPath("$.newCount").value(1))
                .andExpect(jsonPath("$.rows[0].word").value("book"));

        MockMultipartFile oldExcel = new MockMultipartFile("file", "words.xls", "application/vnd.ms-excel", new byte[]{1});
        mvc.perform(multipart("/api/vocabulary/import/preview").file(oldExcel))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_FILE"));
    }

    @Test
    void databaseFailureMidImportSavesNothing() throws Exception {
        execute("""
                CREATE OR REPLACE FUNCTION reject_poison() RETURNS trigger AS $$
                BEGIN
                  IF NEW.word = 'poison' THEN RAISE EXCEPTION 'boom' USING ERRCODE = '23514'; END IF;
                  RETURN NEW;
                END $$ LANGUAGE plpgsql""");
        execute("CREATE TRIGGER poison_guard BEFORE INSERT ON vocabulary_entries "
                + "FOR EACH ROW EXECUTE FUNCTION reject_poison()");
        try {
            commit(previewBody("alpha,가\npoison,독\nomega,오"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("CONCURRENT_CHANGE"));
            assertThat(count()).isZero();
        } finally {
            execute("DROP TRIGGER poison_guard ON vocabulary_entries");
            execute("DROP FUNCTION reject_poison()");
        }
    }

    @Test
    void concurrentImportsOfTheSameRowsCreateNoDuplicates() throws Exception {
        StringBuilder csv = new StringBuilder("word,meaning\n");
        for (int i = 0; i < 200; i++) {
            csv.append("word").append(i).append(",뜻").append(i).append('\n');
        }
        String previewJson = previewBody(csv.toString());

        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                tasks.add(() -> commit(previewJson).andReturn().getResponse().getStatus());
            }
            for (Future<Integer> result : pool.invokeAll(tasks)) {
                assertThat(result.get()).isIn(200, 409);
            }
        } finally {
            pool.shutdown();
        }
        assertThat(count()).isEqualTo(200);
    }

    @Test
    void importsFiveThousandRowsAndRejectsMore() throws Exception {
        StringBuilder csv = new StringBuilder("word,meaning\n");
        for (int i = 0; i < 5000; i++) {
            csv.append("bulkword").append(i).append(",뜻").append(i).append('\n');
        }
        long start = System.nanoTime();
        commit(previewBody(csv.toString())).andExpect(jsonPath("$.created").value(5000));
        System.out.println("5000-row preview+commit took " + (System.nanoTime() - start) / 1_000_000 + " ms");
        assertThat(count()).isEqualTo(5000);

        preview(csv + "extra,뜻\n").andExpect(status().isBadRequest());
    }

    private ResultActions createWord(String word, String meaning, String partOfSpeech) throws Exception {
        return mvc.perform(post("/api/vocabulary").contentType(MediaType.APPLICATION_JSON)
                .content(word(word, meaning, partOfSpeech)));
    }

    private static String word(String word, String meaning, String partOfSpeech) {
        return """
                {"word":"%s","meaning":"%s","partOfSpeech":"%s","language":"en"}""".formatted(word, meaning, partOfSpeech);
    }

    private static String idOf(ResultActions result) throws Exception {
        return result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)
                .replaceAll(".*\"id\":(\\d+).*", "$1");
    }

    private ResultActions preview(String text) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.multipart("/api/vocabulary/import/preview")
                .param("text", text).param("language", "en"));
    }

    private String previewBody(String text) throws Exception {
        return preview(text).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private ResultActions commit(String previewJson) throws Exception {
        return mvc.perform(post("/api/vocabulary/import/commit")
                .contentType(MediaType.APPLICATION_JSON).content(previewJson));
    }

    private long count() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT count(*) FROM vocabulary_entries")) {
            result.next();
            return result.getLong(1);
        }
    }

    private void execute(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
