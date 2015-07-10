package ch.swissbytes.Service.processor;

/**
 * Created by alvaro on 7/2/2015.
 */
public enum TagPDFJasper {

    STYLE("<style>", "</style>"),
    H1("size='32'", ""),//32
    H2("size='24'", ""),//24
    H3("size='18'", ""),//18

    ITALIC("isItalic='true'", ""),
    BOLD("isBold='true'", "pdfFontName='Helvetica-Bold'"),
    UNDERLINED("isUnderline='true'", ""),
    PARAGRAPH("<paragraph>", "</paragraph>");

    final String open;
    final String close;

    TagPDFJasper(String open, String close) {
        this.open = open;
        this.close = close;
    }
}
