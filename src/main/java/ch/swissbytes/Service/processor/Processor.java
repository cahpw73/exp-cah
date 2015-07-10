package ch.swissbytes.Service.processor;

import org.apache.commons.lang.StringUtils;

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

    public String processSnippetText(final String html) {
        if(StringUtils.isEmpty(html)||StringUtils.isBlank(html)){
            return "";
        }
        String copy = html;
        while (copy.length() > 0) {
            String tagFound = findNextTag(copy);
            String textInBetween = getTextInBetween(tagFound, copy);
            if(StringUtils.isNotEmpty(textInBetween)){
                snippets.add(registerTextInBetween(textInBetween));
            }
            if (StringUtils.isNotEmpty(tagFound)) {
                TagHTML target = TagHTML.findTag(tagFound);
                if (target.open.equalsIgnoreCase(tagFound)) {
                    tags.push(TagHTML.findTag(tagFound));
                } else if (tags.size() > 0) {
                    TagHTML topElement = tags.peek();
                    if (topElement.close.equalsIgnoreCase(tagFound)) {
                        tags.pop();
                    }
                }
            }
            if(StringUtils.isNotEmpty(tagFound)) {
                Integer index = copy.toLowerCase().indexOf(tagFound.toLowerCase());
                copy = moveOn(index + tagFound.length(), copy);
            }else{
                copy="";
            }
        }
        return getStyledText();
    }

    private DTOSnippet registerTextInBetween(String textInBetween) {
        List<TagHTML> list = tags.subList(0, tags.size());
        DTOSnippet dto = new DTOSnippet();
        for (TagHTML tag : list) {
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
        }
        dto.setSnippet(textInBetween);
        return dto;
    }

    private String moveOn(Integer index, String html) {
        return index < 0 ? "" : html.substring(index, html.length());
    }

    private String getTextInBetween(String tag, String text) {
        int index =StringUtils.isNotEmpty(tag)? text.toLowerCase().indexOf(tag.toLowerCase()):text.length();
        return text.substring(0, index);
    }

    private String findNextTag(String source) {
        String tagFound = "";
        int minorIndex = Integer.MAX_VALUE;
        for (TagHTML tag : TagHTML.values()) {
            int index = source.toLowerCase().indexOf(tag.open.toLowerCase());
            if (index >= 0) {
                if (index < minorIndex) {
                    minorIndex = index;
                    tagFound = tag.open;
                }
            } else {
                index = source.toLowerCase().indexOf(tag.close.toLowerCase());
                if (index >= 0) {
                    if (index < minorIndex) {
                        minorIndex = index;
                        tagFound = tag.close;
                    }
                }
            }

        }
        return tagFound;
    }

    private String getStyledText() {
        StringBuilder sb = new StringBuilder();
        for (DTOSnippet snippet : snippets) {
            String style = "";
            boolean hasAnyStyle = false;
            if (snippet.isBold()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.BOLD, style);
            }
            if (snippet.isItalic()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.ITALIC, style);
            }
            if (snippet.isUnderlined()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.UNDERLINED, style);
            }
            if (snippet.isH1()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.H1, style);
            }
            if (snippet.isH2()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.H2, style);
            }
            if (snippet.isH3()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.H3, style);
            }
            style = style + snippet.getSnippet();
            if (hasAnyStyle) {
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
       // String s = "algo algo <h2>xxx <b> bold <i>italic<u>underline</u>ppppp</i>abcdef</b>mmmmmmmm</h2>something extra";
        String s = "algo algo <h2>xxx mmmmmmmm<b> more text <i>italic</i> something in the middle last bold </b>";
        System.out.println(new ch.swissbytes.Service.processor.Processor().processSnippetText(s));
        //System.out.println(processor.getStyledText());
        System.out.println("end !");
    }
}
