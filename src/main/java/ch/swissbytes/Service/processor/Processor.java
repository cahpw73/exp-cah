package ch.swissbytes.Service.processor;

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


    public String htmlToJasperPdfStyle(String html) {
        StringBuilder pdfJasperStyle = new StringBuilder();
        String copy = html;
        while (copy.length() > 0) {
            Integer index = processTagHTML(copy);
            if (index > 0) {
                DTOSnippet dto=new DTOSnippet();
                TagHTML tag = tags.peek();
                switch (tag){
                    case BOLD:dto.setIsBold(true);
                        break;
                    case ITALIC:dto.setIsItalic(true);
                        break;
                    case UNDERLINED:dto.setIsUnderlined(true);
                        break;
                    case H1:dto.setIsH1(true);
                        break;
                    case H2:dto.setIsH2(true);
                        break;
                    case H3:dto.setIsH3(true);
                        break;
                }
                dto.setSnippet(copy.substring(0, index));
                snippets.add(dto);
                copy = moveOn(index, copy);
            }
            TagHTML tag = tags.peek();
            String textInBetween = getTextInBetween(tag, copy);
            DTOSnippet dto=new DTOSnippet();
            dto.setSnippet("<style isBold='true'>" + textInBetween + "</style>");
            snippets.add(dto);
            copy = moveOn2(textInBetween.length() + tag.close.length(), copy);
        }
        return pdfJasperStyle.toString();
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
            sb.append(snippet.getSnippet());
        }
        return sb.toString();
    }

    class DTOSnippet{
        private String snippet;
        private boolean isBold;
        private boolean isUnderlined;
        private boolean isH1;
        private boolean isH2;
        private boolean isH3;
        private boolean isItalic;

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public boolean isBold() {
            return isBold;
        }

        public void setIsBold(boolean isBold) {
            this.isBold = isBold;
        }

        public boolean isUnderlined() {
            return isUnderlined;
        }

        public void setIsUnderlined(boolean isUnderlined) {
            this.isUnderlined = isUnderlined;
        }

        public boolean isH1() {
            return isH1;
        }

        public void setIsH1(boolean isH1) {
            this.isH1 = isH1;
        }

        public boolean isH2() {
            return isH2;
        }

        public void setIsH2(boolean isH2) {
            this.isH2 = isH2;
        }

        public boolean isH3() {
            return isH3;
        }

        public void setIsH3(boolean isH3) {
            this.isH3 = isH3;
        }

        public boolean isItalic() {
            return isItalic;
        }

        public void setIsItalic(boolean isItalic) {
            this.isItalic = isItalic;
        }
    }

    public static void main(String[] args) {
        String s = "algo algo <b>xxxxx</b>";
        Processor processor = new Processor();
        processor.htmlToJasperPdfStyle(s);
        System.out.println(processor.getStyledText());
        System.out.println("end !");
    }
}
