package com.example.linguaspace.english;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/english")
public class EnglishStudyController {

    private final EnglishWordRepository words;
    private final EnglishMemoRepository memos;
    private final EnglishExpressionRepository expressions;

    public EnglishStudyController(EnglishWordRepository words,
                                  EnglishMemoRepository memos,
                                  EnglishExpressionRepository expressions) {
        this.words = words;
        this.memos = memos;
        this.expressions = expressions;
    }

    // ---------- Vocabulary ----------

    @GetMapping("/words")
    public List<EnglishWord> listWords() {
        return words.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/words")
    @ResponseStatus(HttpStatus.CREATED)
    public EnglishWord createWord(@RequestBody WordRequest request) {
        requireText(request.word(), "단어는 필수입니다.");
        requireText(request.meaning(), "뜻은 필수입니다.");
        return words.save(new EnglishWord(
                clean(request.word()), clean(request.pronunciation()), clean(request.meaning()),
                clean(request.example()), clean(request.exampleTranslation()),
                clean(request.note()), clean(request.category())));
    }

    @PutMapping("/words/{id}")
    public EnglishWord updateWord(@PathVariable Long id, @RequestBody WordRequest request) {
        requireText(request.word(), "단어는 필수입니다.");
        requireText(request.meaning(), "뜻은 필수입니다.");
        EnglishWord word = words.findById(id).orElseThrow(() -> notFound("단어를 찾을 수 없습니다."));
        word.apply(clean(request.word()), clean(request.pronunciation()), clean(request.meaning()),
                clean(request.example()), clean(request.exampleTranslation()),
                clean(request.note()), clean(request.category()));
        if (request.learned() != null) {
            word.setLearned(request.learned());
        }
        return words.save(word);
    }

    @DeleteMapping("/words/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWord(@PathVariable Long id) {
        if (!words.existsById(id)) throw notFound("단어를 찾을 수 없습니다.");
        words.deleteById(id);
    }

    // ---------- Quick memo ----------

    @GetMapping("/memos")
    public List<EnglishMemo> listMemos() {
        return memos.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/memos")
    @ResponseStatus(HttpStatus.CREATED)
    public EnglishMemo createMemo(@RequestBody MemoRequest request) {
        requireText(request.body(), "메모 내용을 입력해 주세요.");
        return memos.save(new EnglishMemo(clean(request.title()), request.body().trim()));
    }

    @PutMapping("/memos/{id}")
    public EnglishMemo updateMemo(@PathVariable Long id, @RequestBody MemoRequest request) {
        requireText(request.body(), "메모 내용을 입력해 주세요.");
        EnglishMemo memo = memos.findById(id).orElseThrow(() -> notFound("메모를 찾을 수 없습니다."));
        memo.apply(clean(request.title()), request.body().trim());
        return memos.save(memo);
    }

    @DeleteMapping("/memos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMemo(@PathVariable Long id) {
        if (!memos.existsById(id)) throw notFound("메모를 찾을 수 없습니다.");
        memos.deleteById(id);
    }

    // ---------- Expressions ----------

    @GetMapping("/expressions")
    public List<EnglishExpression> listExpressions() {
        return expressions.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/expressions")
    @ResponseStatus(HttpStatus.CREATED)
    public EnglishExpression createExpression(@RequestBody ExpressionRequest request) {
        requireText(request.expression(), "표현은 필수입니다.");
        return expressions.save(new EnglishExpression(
                clean(request.expression()), clean(request.meaning()),
                clean(request.example()), clean(request.tag())));
    }

    @PutMapping("/expressions/{id}")
    public EnglishExpression updateExpression(@PathVariable Long id, @RequestBody ExpressionRequest request) {
        requireText(request.expression(), "표현은 필수입니다.");
        EnglishExpression expression = expressions.findById(id)
                .orElseThrow(() -> notFound("표현을 찾을 수 없습니다."));
        expression.apply(clean(request.expression()), clean(request.meaning()),
                clean(request.example()), clean(request.tag()));
        return expressions.save(expression);
    }

    @DeleteMapping("/expressions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpression(@PathVariable Long id) {
        if (!expressions.existsById(id)) throw notFound("표현을 찾을 수 없습니다.");
        expressions.deleteById(id);
    }

    // ---------- helpers ----------

    private static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private static String clean(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    public record WordRequest(String word, String pronunciation, String meaning, String example,
                              String exampleTranslation, String note, String category, Boolean learned) {
    }

    public record MemoRequest(String title, String body) {
    }

    public record ExpressionRequest(String expression, String meaning, String example, String tag) {
    }
}
