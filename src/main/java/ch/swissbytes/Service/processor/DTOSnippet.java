package ch.swissbytes.Service.processor;

/**
 * Created by alvaro on 7/8/2015.
 */
public class DTOSnippet {

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
