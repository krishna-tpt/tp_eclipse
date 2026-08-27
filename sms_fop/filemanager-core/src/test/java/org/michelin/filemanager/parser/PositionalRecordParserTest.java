package org.michelin.filemanager.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.catalog.InterfaceDefinition.ParserRules;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-290..TC-295 — PositionalRecordParser behavior.
 *
 * The parser is purely mechanical — it has zero knowledge of any specific
 * interface. Tests exercise the splitting / quote-stripping / trimming /
 * empty-is-null / BOM / blank-line / tag-required behaviors in isolation.
 */
class PositionalRecordParserTest {

    private static final Path SIMPLE       = Paths.get("src/test/resources/fixtures/file_parser/simple_4rows.txt");
    private static final Path WITH_EMPTIES = Paths.get("src/test/resources/fixtures/file_parser/with_empties.txt");
    private static final Path WITH_PADDING = Paths.get("src/test/resources/fixtures/file_parser/with_padding.txt");
    private static final Path WITH_BLANKS  = Paths.get("src/test/resources/fixtures/file_parser/with_blank_lines.txt");
    private static final Path WITH_BOM     = Paths.get("src/test/resources/fixtures/file_parser/with_bom.txt");
    private static final Path NO_TAG       = Paths.get("src/test/resources/fixtures/file_parser/no_tag.txt");

    private static ParserRules rules(boolean trim, boolean emptyIsNull) {
        return new ParserRules(";", "\"", "UTF-8", trim, emptyIsNull, ".");
    }

    @Test
    @DisplayName("TC-290: emits one ParsedRecord per non-blank line, tag is field 1, fields are 1-indexed")
    void emitsRecordsWithTagAndFields() throws IOException {
        List<ParsedRecord> all = new PositionalRecordParser(rules(true, true)).parseAll(SIMPLE);

        assertThat(all).hasSize(4);
        assertThat(all).extracting(ParsedRecord::tag)
            .containsExactly("HEADER_FILE", "DATA_A", "DATA_B", "FOOTER_FILE");
        // First row has 5 fields including tag
        assertThat(all.get(0).fieldCount()).isEqualTo(5);
        assertThat(all.get(0).field(1)).isEqualTo("HEADER_FILE");
        assertThat(all.get(0).field(2)).isEqualTo("acme");
        assertThat(all.get(0).field(5)).isEqualTo("batch01");
        // Line numbers preserved
        assertThat(all).extracting(ParsedRecord::lineNumber).containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("TC-291: empty quoted and bare empty fields become null when empty_is_null=true")
    void emptyFieldsBecomeNull() throws IOException {
        List<ParsedRecord> all = new PositionalRecordParser(rules(true, true)).parseAll(WITH_EMPTIES);

        // Row 1: "DATA";"alpha";"";"bravo";"";"charlie"  → 6 fields, positions 3 and 5 are null
        ParsedRecord r1 = all.get(0);
        assertThat(r1.fieldCount()).isEqualTo(6);
        assertThat(r1.field(2)).isEqualTo("alpha");
        assertThat(r1.field(3)).isNull();
        assertThat(r1.field(4)).isEqualTo("bravo");
        assertThat(r1.field(5)).isNull();
        assertThat(r1.field(6)).isEqualTo("charlie");

        // Row 2: bare consecutive delimiters (no quotes) — still null
        ParsedRecord r2 = all.get(1);
        assertThat(r2.field(2)).isEqualTo("only-first");
        assertThat(r2.field(3)).isNull();
        assertThat(r2.field(4)).isNull();
        assertThat(r2.field(5)).isNull();
    }

    @Test
    @DisplayName("TC-292: leading/trailing whitespace stripped when trim=true; whitespace-only becomes null")
    void trimsWhitespaceAndCollapsesToNull() throws IOException {
        List<ParsedRecord> all = new PositionalRecordParser(rules(true, true)).parseAll(WITH_PADDING);

        ParsedRecord r = all.get(0);
        // "DATA";" hello ";"  world  ";" "
        assertThat(r.field(2)).isEqualTo("hello");
        assertThat(r.field(3)).isEqualTo("world");
        assertThat(r.field(4)).isNull();   // " " trimmed to "" → null
    }

    @Test
    @DisplayName("TC-293: UTF-8 BOM at file start is stripped from first field of first record")
    void stripsBomFromFirstLine() throws IOException {
        List<ParsedRecord> all = new PositionalRecordParser(rules(true, true)).parseAll(WITH_BOM);

        assertThat(all).hasSize(2);
        assertThat(all.get(0).tag()).isEqualTo("HEADER_FILE");
        assertThat(all.get(0).tag().charAt(0)).isEqualTo('H'); // no leading BOM character
    }

    @Test
    @DisplayName("TC-294: blank lines are skipped but lineNumber stays accurate")
    void skipsBlankLines() throws IOException {
        List<ParsedRecord> all = new PositionalRecordParser(rules(true, true)).parseAll(WITH_BLANKS);

        assertThat(all).extracting(ParsedRecord::tag)
            .containsExactly("HEADER_FILE", "DATA", "FOOTER_FILE");
        // HEADER_FILE on line 1, DATA on line 3 (blank line 2 skipped), FOOTER_FILE on line 6 (blanks 4,5 skipped)
        assertThat(all).extracting(ParsedRecord::lineNumber)
            .containsExactly(1L, 3L, 6L);
    }

    @Test
    @DisplayName("TC-295: line with empty tag (field 1) raises PositionalParseException")
    void emptyTagRaises() {
        assertThatThrownBy(() -> new PositionalRecordParser(rules(true, true)).parseAll(NO_TAG))
            .isInstanceOf(PositionalParseException.class)
            .hasMessageContaining("record tag");
    }
}
