package ch.swissbytes.Service.processor;

/**
 * Created by alvaro on 7/2/2015.
 */
public enum TagHTML {

    H1("<h1>", "</h1>"),
    H2("<h2>", "</h2>"),
    H3("<h3>", "</h3>"),
    ITALIC("<i>", "</i>"),
    BOLD("<b>", "</b>"),
    UNDERLINED("<u>", "</u>"),
    PARAGRAPH("<p>", "</p>");

    final String open;
    final String close;

    TagHTML(String open, String close) {
        this.open = open;
        this.close = close;
    }
}
