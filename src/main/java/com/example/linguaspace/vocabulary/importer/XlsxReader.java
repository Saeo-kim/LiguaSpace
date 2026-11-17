package com.example.linguaspace.vocabulary.importer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * Reads the first worksheet of an .xlsx file with a streaming parser. A full spreadsheet library
 * would add tens of MB to the image and build the whole workbook in memory, which matters on a
 * 512 MB host. External entities are disabled and decompressed size is capped.
 */
public final class XlsxReader {

    private static final long MAX_UNCOMPRESSED_BYTES = 32L * 1024 * 1024;
    private static final String INVALID_FILE = "엑셀 파일을 읽을 수 없습니다. .xlsx 형식인지 확인해 주세요.";

    private XlsxReader() {
    }

    public static List<TableRow> read(byte[] file, int maxRows) {
        try {
            Map<String, byte[]> parts = unzip(file);
            byte[] sheet = parts.get(firstSheetPath(parts));
            if (sheet == null) {
                throw new ImportFileException(INVALID_FILE);
            }
            List<String> sharedStrings = readSharedStrings(parts.get("xl/sharedStrings.xml"));
            return readSheet(sheet, sharedStrings, maxRows);
        } catch (IOException | XMLStreamException | IllegalArgumentException e) {
            throw new ImportFileException(INVALID_FILE);
        }
    }

    private static Map<String, byte[]> unzip(byte[] file) throws IOException {
        Map<String, byte[]> parts = new HashMap<>();
        long total = 0;
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(file))) {
            byte[] buffer = new byte[8192];
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                boolean wanted = name.equals("xl/workbook.xml")
                        || name.equals("xl/_rels/workbook.xml.rels")
                        || name.equals("xl/sharedStrings.xml")
                        || name.startsWith("xl/worksheets/");
                if (!wanted || entry.isDirectory()) {
                    continue;
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int read;
                while ((read = zip.read(buffer)) != -1) {
                    total += read;
                    if (total > MAX_UNCOMPRESSED_BYTES) {
                        throw new ImportFileException("엑셀 파일의 압축 해제 크기가 너무 큽니다.");
                    }
                    out.write(buffer, 0, read);
                }
                parts.put(name, out.toByteArray());
            }
        } catch (ZipException e) {
            throw new ImportFileException(INVALID_FILE);
        }
        return parts;
    }

    private static String firstSheetPath(Map<String, byte[]> parts) throws XMLStreamException {
        byte[] workbook = parts.get("xl/workbook.xml");
        byte[] relationships = parts.get("xl/_rels/workbook.xml.rels");
        if (workbook == null || relationships == null) {
            throw new ImportFileException(INVALID_FILE);
        }

        String relationshipId = null;
        XMLStreamReader reader = newReader(workbook);
        while (reader.hasNext() && relationshipId == null) {
            if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("sheet")) {
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (reader.getAttributeLocalName(i).equals("id")) {
                        relationshipId = reader.getAttributeValue(i);
                    }
                }
            }
        }

        reader = newReader(relationships);
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamConstants.START_ELEMENT
                    && reader.getLocalName().equals("Relationship")
                    && reader.getAttributeValue(null, "Id").equals(relationshipId)) {
                String target = reader.getAttributeValue(null, "Target");
                return target.startsWith("/") ? target.substring(1) : "xl/" + target;
            }
        }
        throw new ImportFileException(INVALID_FILE);
    }

    private static List<String> readSharedStrings(byte[] xml) throws XMLStreamException {
        List<String> strings = new ArrayList<>();
        if (xml == null) {
            return strings;
        }
        XMLStreamReader reader = newReader(xml);
        StringBuilder current = new StringBuilder();
        int phoneticDepth = 0;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "si" -> current.setLength(0);
                    case "rPh" -> phoneticDepth++;
                    case "t" -> {
                        String text = reader.getElementText();
                        if (phoneticDepth == 0) {
                            current.append(text);
                        }
                    }
                    default -> { }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (reader.getLocalName().equals("si")) {
                    strings.add(current.toString());
                } else if (reader.getLocalName().equals("rPh")) {
                    phoneticDepth--;
                }
            }
        }
        return strings;
    }

    private static List<TableRow> readSheet(byte[] xml, List<String> sharedStrings, int maxRows)
            throws XMLStreamException {
        List<TableRow> rows = new ArrayList<>();
        XMLStreamReader reader = newReader(xml);
        List<String> cells = null;
        int rowNumber = 0;
        int nextColumn = 0;
        int column = 0;
        String cellType = null;
        String value = null;

        while (reader.hasNext() && rows.size() < maxRows) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "row" -> {
                        String number = reader.getAttributeValue(null, "r");
                        rowNumber = number != null ? Integer.parseInt(number) : rowNumber + 1;
                        cells = new ArrayList<>();
                        nextColumn = 0;
                    }
                    case "c" -> {
                        String reference = reader.getAttributeValue(null, "r");
                        column = reference != null ? columnIndex(reference) : nextColumn;
                        nextColumn = column + 1;
                        cellType = reader.getAttributeValue(null, "t");
                        value = null;
                    }
                    case "v" -> value = cellValue(reader.getElementText(), cellType, sharedStrings);
                    case "t" -> value = (value == null ? "" : value) + reader.getElementText();
                    default -> { }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (reader.getLocalName().equals("c") && cells != null && value != null) {
                    while (cells.size() <= column) {
                        cells.add("");
                    }
                    cells.set(column, value);
                } else if (reader.getLocalName().equals("row") && cells != null) {
                    TableRow row = new TableRow(rowNumber, cells, null);
                    if (!row.isBlank()) {
                        rows.add(row);
                    }
                    cells = null;
                }
            }
        }
        return rows;
    }

    private static String cellValue(String raw, String type, List<String> sharedStrings) {
        if ("s".equals(type)) {
            int index = Integer.parseInt(raw.trim());
            if (index < 0 || index >= sharedStrings.size()) {
                throw new ImportFileException(INVALID_FILE);
            }
            return sharedStrings.get(index);
        }
        return raw;
    }

    private static int columnIndex(String reference) {
        int column = 0;
        for (int i = 0; i < reference.length() && Character.isLetter(reference.charAt(i)); i++) {
            column = column * 26 + (Character.toUpperCase(reference.charAt(i)) - 'A' + 1);
        }
        return column - 1;
    }

    private static XMLStreamReader newReader(byte[] xml) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return factory.createXMLStreamReader(new ByteArrayInputStream(xml));
    }
}
