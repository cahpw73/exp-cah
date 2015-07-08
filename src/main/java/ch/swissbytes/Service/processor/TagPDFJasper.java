package ch.swissbytes.Service.processor;

/**
 * Created by alvaro on 7/2/2015.
 */
public enum TagPDFJasper {

    STYLE("<style>", "</style>"),
   // H2("<h2>", "</h2>"),
    //H3("<h3>", "</h3>"),
    ITALIC("isItalic='true'", "</i>"),
    BOLD("isBold='true'", ""),
    UNDERLINED("isUnderlined='true'", "");
    //PARAGRAPH("<p>", "</p>");

    final String open;
    final String close;

    TagPDFJasper(String open, String close) {
        this.open = open;
        this.close = close;
    }
}
