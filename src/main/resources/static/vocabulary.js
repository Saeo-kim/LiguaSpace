"use strict";

(function () {
    const API = "/api/vocabulary";
    const MAX_FILE_BYTES = 5 * 1024 * 1024;
    const MAX_PREVIEW_ROWS_SHOWN = 500;
    const LANGUAGE_NAMES = {
        en: "English · 영어",
        zh: "中文 · Chinese",
        ja: "日本語 · Japanese",
        es: "Español · Spanish",
        ko: "한국어 · Korean"
    };
    const STATUS_LABELS = {
        NEW: "새 단어",
        MERGE: "뜻 추가",
        DUPLICATE: "중복",
        INVALID: "오류"
    };

    const byId = id => document.getElementById(id);
    const form = byId("wordForm");
    const wordInput = byId("word");
    const submitButton = byId("submitButton");
    const cancelEditButton = byId("cancelEditButton");
    const formNotice = byId("formNotice");
    const formHeading = byId("formHeading");
    const wordList = byId("wordList");
    const wordCount = byId("wordCount");
    const searchInput = byId("searchInput");
    const loadMoreButton = byId("loadMoreButton");
    const toast = byId("toast");

    const list = { query: "", language: "", page: 0, requestId: 0 };
    let editingId = null;
    let saving = false;
    let previewRows = [];
    let importing = false;

    function el(tag, className, text) {
        const element = document.createElement(tag);
        if (className) element.className = className;
        if (text !== undefined) element.textContent = text;
        return element;
    }

    function button(className, text, onClick) {
        const element = el("button", className, text);
        element.type = "button";
        element.addEventListener("click", onClick);
        return element;
    }

    let toastTimer;
    function showToast(message, isError) {
        toast.textContent = message;
        toast.className = "toast visible" + (isError ? " error" : "");
        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => { toast.className = "toast"; }, isError ? 6000 : 2200);
    }

    async function api(url, options) {
        let response;
        try {
            response = await fetch(url, options);
        } catch (networkError) {
            throw { message: "서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요." };
        }
        if (response.status === 204) return null;
        const body = await response.json().catch(() => ({}));
        if (!response.ok) {
            throw {
                status: response.status,
                code: body.code,
                existing: body.existing,
                message: body.message || "요청을 처리하지 못했습니다. (" + response.status + ")"
            };
        }
        return body;
    }

    function jsonOptions(method, data) {
        return {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        };
    }

    // ----- Collection -----

    async function loadWords(reset) {
        const requestId = ++list.requestId;
        const page = reset ? 0 : list.page + 1;
        const params = new URLSearchParams({ q: list.query, language: list.language, page: String(page) });
        try {
            const result = await api(API + "?" + params);
            if (requestId !== list.requestId) return;
            list.page = page;
            if (reset) wordList.replaceChildren();
            result.items.forEach(entry => wordList.appendChild(renderCard(entry)));
            if (reset && result.items.length === 0) wordList.appendChild(renderEmptyState());
            wordCount.textContent = result.total.toLocaleString();
            loadMoreButton.hidden = !result.hasMore;
        } catch (error) {
            if (requestId === list.requestId) showToast(error.message, true);
        }
    }

    function renderEmptyState() {
        const box = el("div", "empty-state");
        box.appendChild(el("div", "symbol", "✦"));
        if (list.query || list.language) {
            box.appendChild(el("strong", "", "검색 결과가 없습니다"));
            box.appendChild(el("span", "", "다른 단어로 찾아보거나 필터를 바꿔 보세요."));
        } else {
            box.appendChild(el("strong", "", "아직 저장된 단어가 없습니다"));
            box.appendChild(el("span", "", "위에서 단어를 추가하거나 목록을 한 번에 가져와 보세요."));
        }
        return box;
    }

    function renderCard(entry) {
        const card = el("article", "word-card");

        const top = el("div", "word-top");
        top.appendChild(el("span", "language-tag", LANGUAGE_NAMES[entry.language] || entry.language));
        const date = (entry.createdAt || "").slice(0, 10);
        top.appendChild(el("span", "study-status", entry.learned ? "Learned · " + date : date));
        card.appendChild(top);

        const heading = el("h3", "word", entry.word);
        if (entry.partOfSpeech) heading.appendChild(el("span", "pos-tag", entry.partOfSpeech));
        card.appendChild(heading);

        if (entry.pronunciation) card.appendChild(el("div", "pronunciation", entry.pronunciation));

        const meanings = entry.meaning.split("; ");
        if (meanings.length > 1) {
            const listElement = el("ol", "meaning meaning-list");
            meanings.forEach(meaning => listElement.appendChild(el("li", "", meaning)));
            card.appendChild(listElement);
        } else {
            card.appendChild(el("p", "meaning", entry.meaning));
        }

        if (entry.example || entry.translation) {
            const box = el("div", "example");
            if (entry.example) box.appendChild(el("p", "", entry.example));
            if (entry.translation) box.appendChild(el("p", "", entry.translation));
            card.appendChild(box);
        }
        if (entry.notes) card.appendChild(el("p", "note", entry.notes));
        if (entry.category) card.appendChild(el("span", "category-tag", entry.category));

        const actions = el("div", "card-actions");
        actions.appendChild(button("text-button", entry.learned ? "✓ Learned" : "○ Mark as learned", async () => {
            try {
                const updated = await api(API + "/" + entry.id + "/learned",
                    jsonOptions("PATCH", { learned: !entry.learned }));
                card.replaceWith(renderCard(updated));
            } catch (error) {
                showToast(error.message, true);
            }
        }));
        actions.appendChild(button("text-button", "Edit", () => startEdit(entry)));
        actions.appendChild(button("text-button", "Delete", async () => {
            if (!confirm("'" + entry.word + "' 단어를 삭제할까요?")) return;
            try {
                await api(API + "/" + entry.id, { method: "DELETE" });
                if (editingId === entry.id) stopEdit();
                showToast("삭제했습니다.");
                loadWords(true);
            } catch (error) {
                showToast(error.message, true);
            }
        }));
        card.appendChild(actions);
        return card;
    }

    let searchTimer;
    searchInput.addEventListener("input", () => {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            list.query = searchInput.value.trim();
            loadWords(true);
        }, 200);
    });

    document.querySelectorAll(".filter-button").forEach(filter => {
        filter.addEventListener("click", () => {
            document.querySelectorAll(".filter-button").forEach(other => other.classList.remove("active"));
            filter.classList.add("active");
            list.language = filter.dataset.language;
            loadWords(true);
        });
    });

    loadMoreButton.addEventListener("click", () => loadWords(false));

    // ----- Add and edit -----

    const FIELDS = ["word", "meaning", "partOfSpeech", "language", "category",
        "pronunciation", "example", "translation", "notes"];

    function readForm() {
        const data = {};
        FIELDS.forEach(field => { data[field] = byId(field).value.trim(); });
        return data;
    }

    function clearForm() {
        ["word", "meaning", "partOfSpeech", "pronunciation", "example", "translation", "notes"]
            .forEach(field => { byId(field).value = ""; });
        wordInput.focus();
    }

    function hideNotice() {
        formNotice.hidden = true;
        formNotice.replaceChildren();
    }

    function showNotice(message, actions) {
        formNotice.replaceChildren(el("span", "", message));
        actions.forEach(action => formNotice.appendChild(action));
        formNotice.hidden = false;
        if (actions[0]) actions[0].focus();
    }

    function startEdit(entry) {
        editingId = entry.id;
        selectTab("single");
        FIELDS.forEach(field => { byId(field).value = entry[field] || ""; });
        formHeading.textContent = "Edit word";
        submitButton.textContent = "Save changes";
        cancelEditButton.hidden = false;
        hideNotice();
        form.scrollIntoView({ behavior: "smooth", block: "center" });
        wordInput.focus();
    }

    function stopEdit() {
        editingId = null;
        formHeading.textContent = "Add a new word";
        submitButton.textContent = "+ Add to my collection";
        cancelEditButton.hidden = true;
        hideNotice();
        clearForm();
    }

    async function saveWord(merge) {
        if (saving) return;
        const data = readForm();
        if (!data.word || !data.meaning) {
            showToast("단어와 한국어 뜻을 입력해 주세요.", true);
            (data.word ? byId("meaning") : wordInput).focus();
            return;
        }

        saving = true;
        submitButton.disabled = true;
        try {
            const wasEditing = editingId !== null;
            const saved = wasEditing
                ? await api(API + "/" + editingId, jsonOptions("PUT", data))
                : await api(API + (merge ? "?merge=true" : ""), jsonOptions("POST", data));
            showToast((wasEditing ? "수정했습니다: " : "저장했습니다: ") + saved.word);
            if (wasEditing) stopEdit(); else { hideNotice(); clearForm(); }
            loadWords(true);
        } catch (error) {
            handleSaveError(error);
        } finally {
            saving = false;
            submitButton.disabled = false;
        }
    }

    function handleSaveError(error) {
        if (error.code === "MERGEABLE") {
            showNotice(
                error.message + " (기존 뜻: " + error.existing.meaning + ")",
                [
                    button("primary-button small", "새 뜻 추가", () => saveWord(true)),
                    button("secondary-button small", "취소", () => { hideNotice(); wordInput.focus(); })
                ]
            );
        } else if (error.code === "DUPLICATE") {
            showNotice(
                error.message + " (" + error.existing.word + ": " + error.existing.meaning + ")",
                [
                    button("secondary-button small", "기존 단어 편집", () => startEdit(error.existing)),
                    button("secondary-button small", "닫기", () => { hideNotice(); wordInput.focus(); })
                ]
            );
        } else {
            showToast(error.message, true);
        }
    }

    form.addEventListener("submit", event => {
        event.preventDefault();
        saveWord(false);
    });
    cancelEditButton.addEventListener("click", stopEdit);

    form.addEventListener("keydown", event => {
        if (event.key === "Enter" && (event.ctrlKey || event.metaKey)) {
            event.preventDefault();
            saveWord(false);
        }
    });

    document.addEventListener("keydown", event => {
        const typing = /^(INPUT|TEXTAREA|SELECT)$/.test(event.target.tagName);
        if (event.key === "/" && !typing) {
            event.preventDefault();
            searchInput.focus();
        } else if (event.key === "Escape") {
            if (editingId !== null) stopEdit();
            else if (event.target === searchInput) { searchInput.value = ""; list.query = ""; loadWords(true); }
        }
    });

    // ----- Tabs -----

    function selectTab(name) {
        document.querySelectorAll("[data-tab]").forEach(tab => {
            const active = tab.dataset.tab === name;
            tab.classList.toggle("active", active);
            tab.setAttribute("aria-selected", String(active));
        });
        byId("singlePanel").hidden = name !== "single";
        byId("importPanel").hidden = name !== "import";
    }

    document.querySelectorAll("[data-tab]").forEach(tab => {
        tab.addEventListener("click", () => {
            selectTab(tab.dataset.tab);
            (tab.dataset.tab === "import" ? importText : wordInput).focus();
        });
    });

    // ----- Bulk import -----

    const importText = byId("importText");
    const importFile = byId("importFile");
    const previewButton = byId("previewButton");
    const previewArea = byId("previewArea");
    const importSummary = byId("importSummary");
    const previewBody = byId("previewBody");
    const previewNote = byId("previewNote");
    const commitButton = byId("commitButton");
    const importResult = byId("importResult");

    function resetPreview() {
        previewRows = [];
        previewArea.hidden = true;
        previewBody.replaceChildren();
    }

    async function requestPreview() {
        const file = importFile.files[0];
        const text = importText.value;
        importResult.hidden = true;
        if (!file && !text.trim()) {
            showToast("파일을 선택하거나 내용을 붙여 넣어 주세요.", true);
            return;
        }
        if (file && file.size > MAX_FILE_BYTES) {
            showToast("파일은 5MB까지 올릴 수 있습니다.", true);
            return;
        }

        const data = new FormData();
        if (file) data.append("file", file); else data.append("text", text);
        data.append("language", byId("importLanguage").value);
        data.append("category", byId("importCategory").value.trim());

        previewButton.disabled = true;
        try {
            renderPreview(await api(API + "/import/preview", { method: "POST", body: data }));
        } catch (error) {
            resetPreview();
            showToast(error.message, true);
        } finally {
            previewButton.disabled = false;
        }
    }

    function renderPreview(preview) {
        previewRows = preview.rows;
        importSummary.replaceChildren(
            summaryChip("status-new", "새 단어", preview.newCount),
            summaryChip("status-merge", "뜻 추가", preview.mergeCount),
            summaryChip("status-duplicate", "중복", preview.duplicateCount),
            summaryChip("status-invalid", "오류", preview.invalidCount)
        );

        // Problems first, so they are visible without scrolling through a long list.
        const ordered = [...preview.rows].sort((a, b) =>
            (a.status === "INVALID" ? 0 : 1) - (b.status === "INVALID" ? 0 : 1) || a.rowNumber - b.rowNumber);
        const shown = ordered.slice(0, MAX_PREVIEW_ROWS_SHOWN);
        previewBody.replaceChildren(...shown.map(renderPreviewRow));
        previewNote.hidden = ordered.length <= shown.length;
        previewNote.textContent = "앞의 " + shown.length + "행만 표시했습니다. 전체 "
            + ordered.length + "행이 처리됩니다.";

        const saveCount = preview.newCount + preview.mergeCount;
        commitButton.textContent = saveCount > 0 ? saveCount + "개 저장하기" : "저장할 행이 없습니다";
        commitButton.disabled = saveCount === 0;
        previewArea.hidden = false;
        previewArea.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }

    function summaryChip(statusClass, label, count) {
        const chip = el("span", "summary-chip " + statusClass, label + " ");
        chip.appendChild(el("strong", "", String(count)));
        return chip;
    }

    function renderPreviewRow(row) {
        const tr = el("tr", row.status === "INVALID" ? "row-invalid" : "");
        tr.appendChild(el("td", "", String(row.rowNumber)));
        const statusCell = el("td");
        statusCell.appendChild(el("span", "status-badge status-" + row.status.toLowerCase(), STATUS_LABELS[row.status]));
        tr.appendChild(statusCell);
        tr.appendChild(el("td", "cell-word", row.word));
        tr.appendChild(el("td", "", row.meaning));
        tr.appendChild(el("td", "", row.partOfSpeech));
        tr.appendChild(el("td", "", row.example));
        tr.appendChild(el("td", "", row.translation));
        tr.appendChild(el("td", "cell-message", row.message));
        return tr;
    }

    async function commitImport() {
        if (importing || previewRows.length === 0) return;
        importing = true;
        commitButton.disabled = true;
        commitButton.textContent = "저장 중...";
        try {
            const result = await api(API + "/import/commit", jsonOptions("POST", { rows: previewRows }));
            renderImportResult(result);
            resetPreview();
            importText.value = "";
            importFile.value = "";
            loadWords(true);
        } catch (error) {
            showToast(error.message + " (저장된 행은 없습니다.)", true);
            commitButton.disabled = false;
            commitButton.textContent = "다시 시도";
        } finally {
            importing = false;
        }
    }

    function renderImportResult(result) {
        const children = [
            el("strong", "", "가져오기 완료"),
            el("p", "", "새 단어 " + result.created + "개 · 뜻 추가 " + result.merged
                + "개 · 중복 건너뜀 " + result.skipped + "개 · 실패 " + result.failed + "개")
        ];
        if (result.failures.length > 0) {
            const failures = el("ul", "failure-list");
            result.failures.slice(0, 100).forEach(failure => failures.appendChild(
                el("li", "", failure.rowNumber + "행" + (failure.word ? " (" + failure.word + ")" : "")
                    + ": " + failure.message)));
            children.push(failures);
        }
        importResult.replaceChildren(...children);
        importResult.hidden = false;
    }

    previewButton.addEventListener("click", requestPreview);
    commitButton.addEventListener("click", commitImport);
    importFile.addEventListener("change", () => { if (importFile.files[0]) requestPreview(); });
    importText.addEventListener("input", resetPreview);
    importText.addEventListener("keydown", event => {
        if (event.key === "Enter" && (event.ctrlKey || event.metaKey)) {
            event.preventDefault();
            requestPreview();
        }
    });
    byId("clearImportButton").addEventListener("click", () => {
        importText.value = "";
        importFile.value = "";
        importResult.hidden = true;
        resetPreview();
        importText.focus();
    });

    loadWords(true);
})();
