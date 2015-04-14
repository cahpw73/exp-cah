package ch.swissbytes.fqmes.util;


import ch.swissbytes.fqmes.model.entities.*;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/26/14.
 */
public class DownloadFile implements Serializable {


    private static final Logger log = Logger.getLogger(DownloadFile.class.getName());

    public void downloadAttachedFileOnComment(final AttachmentComment attachmentComment){
        if(attachmentComment!=null){
            download(attachmentComment.getMimeType(),attachmentComment.getFileName(),attachmentComment.getFile());
        }
    }

    public void downloadAttachedFileOnScopeSupplly(final AttachmentScopeSupply attachmentScopeSupply){
        if(attachmentScopeSupply!=null){
            download(attachmentScopeSupply.getMimeType(), attachmentScopeSupply.getFileName(), attachmentScopeSupply.getFile());
        }
    }

    private void download(final String mimeType,final String name, final byte[]file){
        log.info("private void download(final String mimeType="+mimeType+",final String name="+name+", final byte[]file)");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType(mimeType);
        response.addHeader("Content-disposition", "attachment; filename=\"" + name + "\"");
        try {
            response.setContentLength(file.length);
            ServletOutputStream os = response.getOutputStream();
            os.write(file);
            os.flush();
            os.close();
            facesContext.responseComplete();
        } catch (Exception e) {
//                log.error("\nFailure : " + e.toString() + "\n");
        }
    }


}
