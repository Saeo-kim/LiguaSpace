package com.example.linguaspace.vocabulary;

import com.example.linguaspace.vocabulary.importer.DelimitedTextParser;
import com.example.linguaspace.vocabulary.importer.ImportFileException;
import com.example.linguaspace.vocabulary.importer.RowMapper;
import com.example.linguaspace.vocabulary.importer.TableRow;
import com.example.linguaspace.vocabulary.importer.TextDecoder;
import com.example.linguaspace.vocabulary.importer.XlsxReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImportParsingTest {

    private static List<RowMapper.MappedRow> map(String text) {
        return RowMapper.map(DelimitedTextParser.parse(text), "en", "");
    }

    @Test
    void parsesTheDocumentedFormat() {
        List<RowMapper.MappedRow> rows = map("""
                Word,Meaning,PartOfSpeech,Example,Translation,Notes
                accomplish,성취하다,verb,I accomplished my goal.,나는 목표를 달성했다.,
                """);

        assertThat(rows).hasSize(1);
        VocabularyInput input = rows.get(0).input();
        assertThat(rows.get(0).number()).isEqualTo(2);
        assertThat(rows.get(0).error()).isNull();
        assertThat(input.word()).isEqualTo("accomplish");
        assertThat(input.meaning()).isEqualTo("성취하다");
        assertThat(input.partOfSpeech()).isEqualTo("verb");
        assertThat(input.example()).isEqualTo("I accomplished my goal.");
        assertThat(input.translation()).isEqualTo("나는 목표를 달성했다.");
        assertThat(input.language()).isEqualTo("en");
    }

    @Test
    void worksWithoutAHeaderRow() {
        List<RowMapper.MappedRow> rows = map("run,달리다,v\nwalk,걷다,v");
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).number()).isEqualTo(1);
        assertThat(rows.get(1).input().meaning()).isEqualTo("걷다");
    }

    @Test
    void handlesQuotedCommasEscapedQuotesAndLineBreaks() {
        List<RowMapper.MappedRow> rows = map("""
                word,meaning,example
                "run, fast","달리다","He said ""go"".
                second line"
                next,다음,
                """);

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).input().word()).isEqualTo("run, fast");
        assertThat(rows.get(0).input().example()).isEqualTo("He said \"go\".\nsecond line");
        assertThat(rows.get(1).number()).isEqualTo(4);
    }

    @Test
    void readsTabSeparatedTextPastedFromASpreadsheet() {
        List<RowMapper.MappedRow> rows = map("단어\t뜻\t품사\r\nbook\t책, 도서\t명사\r\n");
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).input().meaning()).isEqualTo("책, 도서");
        assertThat(rows.get(0).input().partOfSpeech()).isEqualTo("명사");
    }

    @Test
    void reportsTooManyColumnsAndUnclosedQuotes() {
        assertThat(map("run,달리다,verb,I ran, then stopped.,나는 달렸다,메모").get(0).error()).contains("열이 너무 많습니다");
        assertThat(map("run,\"달리다").get(0).error()).contains("따옴표");
    }

    @Test
    void ignoresBlankLinesAndAllowsSpecialCharactersInOptionalCells() {
        assertThat(map("\n\nrun,달리다\n,,,\n\nwalk,걷다\n")).hasSize(2);
    }

    @Test
    void decodesUtf8WithAndWithoutBom() {
        assertThat(TextDecoder.decode("성취".getBytes(StandardCharsets.UTF_8))).isEqualTo("성취");
        byte[] withBom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'a'};
        assertThat(TextDecoder.decode(withBom)).isEqualTo("a");
    }

    @Test
    void decodesKoreanExcelCsv() {
        assertThat(TextDecoder.decode("성취하다,달성".getBytes(Charset.forName("MS949")))).isEqualTo("성취하다,달성");
    }

    @Test
    void readsXlsxWithSharedStringsPhoneticsAndGaps() throws IOException {
        byte[] file = workbook(Map.of(
                "xl/sharedStrings.xml", """
                        <sst xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                        <si><t>Word</t></si><si><t>Meaning</t></si><si><t>Notes</t></si>
                        <si><t>accomplish</t></si>
                        <si><r><t>성취</t></r><r><t>하다</t></r><rPh sb="0" eb="1"><t>ignored</t></rPh></si>
                        </sst>""",
                "xl/worksheets/sheet1.xml", """
                        <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>
                        <row r="1"><c r="A1" t="s"><v>0</v></c><c r="B1" t="s"><v>1</v></c><c r="D1" t="s"><v>2</v></c></row>
                        <row r="3"><c r="A3" t="s"><v>3</v></c><c r="B3" t="s"><v>4</v></c>
                        <c r="D3" t="inlineStr"><is><t>inline note</t></is></c></row>
                        </sheetData></worksheet>"""
        ));

        List<TableRow> table = XlsxReader.read(file, 100);
        List<RowMapper.MappedRow> rows = RowMapper.map(table, "en", "");

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).number()).isEqualTo(3);
        assertThat(rows.get(0).input().word()).isEqualTo("accomplish");
        assertThat(rows.get(0).input().meaning()).isEqualTo("성취하다");
        assertThat(rows.get(0).input().notes()).isEqualTo("inline note");
    }

    @Test
    void rejectsInvalidXlsxAndExternalEntities() throws IOException {
        assertThatThrownBy(() -> XlsxReader.read("not a zip".getBytes(StandardCharsets.UTF_8), 10))
                .isInstanceOf(ImportFileException.class);

        byte[] hostile = workbook(Map.of("xl/worksheets/sheet1.xml", """
                <!DOCTYPE x [<!ENTITY e SYSTEM "file:///etc/passwd">]>
                <worksheet><sheetData><row r="1"><c r="A1" t="str"><v>&e;</v></c></row></sheetData></worksheet>"""));
        assertThatThrownBy(() -> XlsxReader.read(hostile, 10)).isInstanceOf(ImportFileException.class);
    }

    @Test
    void rejectsDecompressionBombs() throws IOException {
        byte[] bomb = workbook(Map.of("xl/worksheets/sheet1.xml", " ".repeat(33 * 1024 * 1024)));
        assertThatThrownBy(() -> XlsxReader.read(bomb, 10))
                .isInstanceOf(ImportFileException.class)
                .hasMessageContaining("너무 큽니다");
    }

    private static byte[] workbook(Map<String, String> extraParts) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            write(zip, "xl/workbook.xml", """
                    <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
                     xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                    <sheets><sheet name="S" sheetId="1" r:id="rId1"/></sheets></workbook>""");
            write(zip, "xl/_rels/workbook.xml.rels", """
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="worksheet" Target="worksheets/sheet1.xml"/></Relationships>""");
            for (Map.Entry<String, String> part : extraParts.entrySet()) {
                write(zip, part.getKey(), part.getValue());
            }
        }
        return bytes.toByteArray();
    }

    private static void write(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}
