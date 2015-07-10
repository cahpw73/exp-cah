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
    private boolean isPdf=false;

    public Processor(boolean isPdf) {
        tags = new Stack<>();
        snippets = new ArrayList<>();
        this.isPdf=isPdf;
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
                    dto.setIsH2(false);
                    dto.setIsH3(false);
                    break;
                case H2:
                    dto.setIsH2(true);
                    dto.setIsH1(false);
                    dto.setIsH3(false);
                    break;
                case H3:
                    dto.setIsH3(true);
                    dto.setIsH1(false);
                    dto.setIsH2(false);
                    break;
                case PARAGRAPH_OPENING:
                    dto.setOpenParagraph(true);
                    break;
                case PARAGRAPH_CLOSING:
                    dto.setCloseParagraph(true);
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
            if(tag.ordinal()!=TagHTML.ITALIC_BOLD.ordinal()) {
                int index = tag.open!=null?source.toLowerCase().indexOf(tag.open.toLowerCase()):-1;
                if (index >= 0) {
                    if (index < minorIndex) {
                        minorIndex = index;
                        tagFound = tag.open;
                    }
                } else {
                    index = tag.close!=null?source.toLowerCase().indexOf(tag.close.toLowerCase()):-1;
                    if (index >= 0) {
                        if (index < minorIndex) {
                            minorIndex = index;
                            tagFound = tag.close;
                        }
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
            if (snippet.isBold()&&!snippet.isItalic()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.BOLD, style);
            }
            if (snippet.isItalic()&&!snippet.isBold()) {
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.ITALIC, style);
            }
            if(snippet.isItalic()&&snippet.isBold()){
                hasAnyStyle = true;
                style = creatingProperty(TagHTML.ITALIC_BOLD, style);
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

            style = style + (snippet.isOpenParagraph()?System.getProperty("line.separator"):"")+snippet.getSnippet();
            if (hasAnyStyle) {
                style = style + TagPDFJasper.STYLE.close;
            }
            style=style+(snippet.isCloseParagraph()?System.getProperty("line.separator"):"");
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
                if(isPdf){
                    newText = insertProperty(newText, TagPDFJasper.BOLD.close);
                }
                break;
            case UNDERLINED:
                newText = insertProperty(text, TagPDFJasper.UNDERLINED.open);
                break;
            case ITALIC:
                newText = insertProperty(text, TagPDFJasper.ITALIC.open);
                if(isPdf){
                    newText = insertProperty(newText, TagPDFJasper.ITALIC.close);
                }
                break;
            case ITALIC_BOLD:
                newText = insertProperty(text, TagPDFJasper.ITALIC.open);
                newText = insertProperty(newText, TagPDFJasper.BOLD.open);
                if(isPdf){
                    newText = insertProperty(newText, TagPDFJasper.ITALIC_BOLD.open);
                }
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
        return StringUtils.isEmpty(text)||StringUtils.isBlank(text)?"": text.substring(0, text.length() - 1) + " " + property + text.substring(text.length() - 1, text.length());
    }

}
