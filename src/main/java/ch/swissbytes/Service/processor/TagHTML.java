package ch.swissbytes.Service.processor;

import org.apache.commons.lang.StringUtils;

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
    PARAGRAPH_OPENING("<paragraph>", "<paraaaaa"),
    PARAGRAPH_CLOSING("</paragraph>", "</parrra"),
    ITALIC_BOLD("","");



    final String open;
    final String close;

    TagHTML(String open, String close) {
        this.open = open;
        this.close = close;
    }

    public static TagHTML findTag(String tag){
        TagHTML tagFound=null;
        if(StringUtils.isNotEmpty(tag)&&StringUtils.isNotBlank(tag)) {
            for (TagHTML tagHTML : TagHTML.values()) {
                if (tagHTML.open.equalsIgnoreCase(tag)||tagHTML.close.equalsIgnoreCase(tag)){
                    tagFound=tagHTML;
                    break;
                }
            }
        }
        return tagFound;
    }

}
