package ch.swissbytes.fqmes.report.util;


/**
 * @author Christian Alba
 */
public enum DocTypeEnum {

    PDF(".pdf"), HTML(".html"), XLS(".xls"), CSV(".csv");

    private final String ext;



    DocTypeEnum(String s) {
        this.ext = s;
    }

    public String getExt() {
        return ext;
    }
}
