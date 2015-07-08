package ch.swissbytes.Service.processor;

/**
 * Created by alvaro on 7/2/2015.
 */
public enum TagPDFJasper {

    STYLE("<style>", "</style>"),
    H1("size='18'", ""),//18
    H2("size='14'", ""),//14
    H3("size='12'", ""),//12

    ITALIC("isItalic='true'", ""),
    BOLD("isBold='true'", ""),
    UNDERLINED("isUnderline='true'", "");
    //PARAGRAPH("<p>", "</p>");

    final String open;
    final String close;

    TagPDFJasper(String open, String close) {
        this.open = open;
        this.close = close;
    }
}
