package ch.swissbytes.fqmes.model.interfaces;

/**
 * Created by alvaro on 9/15/14.
 */
public interface ManageFile {

    void setFileName(String fileName);

    String getFileName();

    void setMimeType(String mimeType);

    String getMimeType();

    void setFile(byte[] stream);

    byte[] getFile();
}
