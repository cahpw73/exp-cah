package ch.swissbytes.Service.processor;

import org.apache.commons.lang.StringUtils;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by alvaro on 7/2/2015.
 */
public class Processor {

    private Stack<TagHTML> tags;
    private List<DTOSnippet> snippets;

    public Processor() {
        tags = new Stack<>();
        snippets = new ArrayList<>();
    }

    public String htmlToJasperPdfStyle(final String html) {
        StringBuilder pdfJasperStyle = new StringBuilder();
        String copy = html;
        while (copy.length() > 0) {
            Integer index = processTagHTML(copy);
            if (index > 0) {
                DTOSnippet dto = new DTOSnippet();
                dto.setSnippet(copy.substring(0, index));
                snippets.add(dto);
                copy = moveOn(index, copy);
            }
            TagHTML tag = tags.peek();
            String textInBetween = getTextInBetween(tag, copy);
            snippets.add(registerTextInBetween(textInBetween, tag));
            copy = moveOn2(textInBetween.length() + tag.close.length(), copy);
        }
        return pdfJasperStyle.toString();
    }

    private DTOSnippet registerTextInBetween(String textInBetween, TagHTML tag) {
        DTOSnippet dto = new DTOSnippet();
        switch (tag) {
            case BOLD:
                dto.setIsBold(true);
                break;
            case ITALIC:
                dto.setIsItalic(true);
                break;
            case UNDERLINED:
                dto.setIsUnderlined(true);
                break;
            case H1:
                dto.setIsH1(true);
                break;
            case H2:
                dto.setIsH2(true);
                break;
            case H3:
                dto.setIsH3(true);
                break;
        }
        dto.setSnippet(textInBetween);
        return dto;
    }

    private String moveOn(Integer index, String html) {
        return index < 0 ? "" : html.substring(index + tags.peek().open.length(), html.length());
    }

    private String moveOn2(Integer index, String html) {
        return index < 0 ? "" : html.substring(index, html.length());
    }

    private String getTextInBetween(TagHTML tag, String text) {
        int index = text.toLowerCase().indexOf(tag.close.toLowerCase());
        return text.substring(0, index);
    }

    private Integer processTagHTML(String source) {
        int index = -1;
        for (TagHTML tag : TagHTML.values()) {
            index = source.toLowerCase().indexOf(tag.open.toLowerCase());
            if (index >= 0) {
                tags.push(tag);
                break;
            }
        }
        return index;
    }

    public String getStyledText() {
        StringBuilder sb = new StringBuilder();
        for (DTOSnippet snippet : snippets) {
            String style = "";
            boolean hasAnyStyle=false;
            if (snippet.isBold()) {
                hasAnyStyle=true;
                style = creatingProperty(TagHTML.BOLD, style);
            }
            if (snippet.isItalic()) {
                hasAnyStyle=true;
                style = creatingProperty(TagHTML.ITALIC, style);
            }
            if (snippet.isUnderlined()) {
                hasAnyStyle=true;
                style = creatingProperty(TagHTML.UNDERLINED, style);
            }
            if (snippet.isH1()) {
                hasAnyStyle=true;
                style = creatingProperty(TagHTML.H1, style);
            }
            if (snippet.isH2()) {
                hasAnyStyle=true;
                style = creatingProperty(TagHTML.H2, style);
            }
            if (snippet.isH3()) {
                hasAnyStyle=true;
                style = creatingProperty(TagHTML.H3, style);
            }
            style = style + snippet.getSnippet();
            if(hasAnyStyle) {
                style = style + TagPDFJasper.STYLE.close;
            }
            sb.append(style);
        }
        return sb.toString();
    }

    private String creatingProperty(TagHTML tagHtml, String base) {
        if (StringUtils.isEmpty(base) || StringUtils.isBlank(base)) {
            base = TagPDFJasper.STYLE.open;
        }
        return insert(base, tagHtml);
    }


    private String insert(String text, TagHTML tag) {
        String newText = "";
        switch (tag) {
            case BOLD:
                newText = insertProperty(text, TagPDFJasper.BOLD.open);
                break;
            case UNDERLINED:
                newText = insertProperty(text, TagPDFJasper.UNDERLINED.open);
                break;
            case ITALIC:
                newText = insertProperty(text, TagPDFJasper.ITALIC.open);
                break;
            case H1:
                newText = insertProperty(text, TagPDFJasper.H1.open);
                break;
            case H2:
                newText = insertProperty(text, TagPDFJasper.H2.open);
                break;
            case H3:
                newText = insertProperty(text, TagPDFJasper.H3.open);
                break;
        }
        return newText;
    }

    private String insertProperty(String text, String property) {
        return text.substring(0, text.length() - 1) + " " + property + text.substring(text.length() - 1, text.length());
    }

    public static void main(String[] args) {
        String s = "algo algo <h2>xxxxx</h2>";
        Processor processor = new Processor();
        processor.htmlToJasperPdfStyle(s);
        System.out.println(processor.getStyledText());
        System.out.println("end !");
    }
}
