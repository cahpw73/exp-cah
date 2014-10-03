/*-*
 * FILENAME  :
 *    $HeadURL$
 *
 * STATUS  :
 *    $Revision$
 *
 *    $Author$
 *    $Date$
 *  
 *    
 * Copyright (c) 2006 SwissBytes Ltda. All rights reserved.
 *
 ****************************************************************/

package ch.swissbytes.fqmes.report.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Geremias Gonzalez
 */
public class ReportFileUtils {

    private static final String FOLDER_REPORT = "reports/";

    private static final String FOLDER_SUB_REPORT = FOLDER_REPORT + "subreports/";

    private static final String FOLDER_IMAGES = FOLDER_REPORT + "images/";

    public static JasperReport loadSubReport(String nameReport) {
        String pathFile = FOLDER_SUB_REPORT + nameReport + ".jasper";
        return loadReportFile(pathFile);
    }

    public static InputStream getImageAsStream(String filename) throws IOException {
        return getResourceAsStream(FOLDER_IMAGES + filename);
    }

    public static JasperReport loadReport(String nameReport) {
        String pathFile = FOLDER_REPORT + nameReport + ".jasper";
        return loadReportFile(pathFile);
    }

    private static JasperReport loadReportFile(String reportNamePath) {
        System.out.println("private static JasperReport loadReportFile(String reportNamePath="+reportNamePath+") ");
        JasperReport jasperReport = null;
        InputStream inputStream = null;
        try {
            inputStream = getResourceAsStream(reportNamePath);
            if (inputStream == null) {
                throw new IOException("Name reports " + reportNamePath + " not found. The reports design must be compiled first.");
            }
            jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
        } catch (JRException ex) {
            ex.printStackTrace();
            //LOG.error("JRException ", ex);
        } catch (IOException e) {
            e.printStackTrace();
            //LOG.error("IOException ", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return jasperReport;
    }

    public static InputStream getResourceAsStream(String resource)
     throws IOException
   {
     ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
     InputStream stream = classLoader.getResourceAsStream(resource);
     if (stream == null) {
       stream = classLoader.getResourceAsStream(resource.substring(resource.indexOf("/") + 1));
     }
     if (stream == null) {
       stream = new FileInputStream(resource);
     }
     return stream;
   }


}
