package ch.swissbytes.Service.processor;

/**
 * Created by alvaro on 7/2/2015.
 */
public enum TagPDFJasper {

    STYLE("<style>", "</style>"),
    H1("size='24'", ""),//32--24
    H2("size='18'", ""),//24--18
    H3("size='16'", ""),//18--16

    ITALIC("isItalic='true'", "pdfFontName='Helvetica-Oblique'"),
    BOLD("isBold='true'", "pdfFontName='Helvetica-Bold'"),
    ITALIC_BOLD("pdfFontName='Helvetica-BoldOblique'", ""),
    UNDERLINED("isUnderline='true'", ""),
    PARAGRAPH(null, null);

    final String open;
    final String close;

    TagPDFJasper(String open, String close) {
        this.open = open;
        this.close = close;
    }
}
