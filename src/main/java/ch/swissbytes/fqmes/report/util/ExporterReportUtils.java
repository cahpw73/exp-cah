package ch.swissbytes.fqmes.report.util;


import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import org.apache.commons.lang3.StringUtils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

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

/**
 * @author Geremias Gonzalez
 */
public class ExporterReportUtils {

    public static JRAbstractExporter crateExporterReportAction(final DocTypeEnum docType, final JasperPrint jasperPrint, final OutputStream out, final HttpServletResponse response,
        final String reportName) throws JRException {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);

        JRAbstractExporter exporter = crateExporterReport(docType, jasperPrint, request.getContextPath(), response, reportName);
        exporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, out);

        return exporter;
    }

    public static JRAbstractExporter crateExporterScheduleReport(final DocTypeEnum docType, final JasperPrint jasperPrint, final OutputStream out,
                                                               final String reportName) throws JRException {
        //JRAbstractExporter exporter = crateExporterReport(docType, jasperPrint, request.getContextPath(), response, reportName);
        JRAbstractExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, out);

        return exporter;
    }

    public static JRAbstractExporter crateExporterReportJobs(final DocTypeEnum docType, final JasperPrint jasperPrint, final String contextPath, final String reportName) throws JRException {
        return crateExporterReport(docType, jasperPrint, contextPath, null, reportName);
    }

    private static JRAbstractExporter crateExporterReport(final DocTypeEnum docType, final JasperPrint jasperPrint, final String contextPath, final HttpServletResponse response,
        final String reportName) throws JRException {

        JRAbstractExporter exporter = null;

        String onlyFilename = getOnlyReportNameFormat(reportName);
        switch (docType) {
            case HTML: {
                exporter = new JRHtmlExporter();
                // exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                // exporter.setParameter(JRHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
                // exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "../servlets/image?image=");
                // exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, FlyerImageHandler.getRootTmpPath());
                // exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);

                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, contextPath + "/image?image=");

                if (response != null) {
                    response.setContentType("text/html; charset=UTF-8");
                }
                break;
            }
            case XLS: {
                exporter = new JRXlsExporter();
                exporter.setParameter(JRExporterParameter.IGNORE_PAGE_MARGINS, true);
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, true);
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true);

                if (response != null) {
                    response.setContentType("application/vnd.ms-excel");
                    response.setHeader("Content-Disposition", "attachment; filename=" + onlyFilename + docType.getExt());
                }

                break;
            }
            case CSV: {
                exporter = new JRCsvExporter();
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, onlyFilename + docType.getExt());

                if (response != null) {
                    response.setContentType("text/csv");
                    response.setHeader("Content-Disposition", "attachment; filename=" + onlyFilename + docType.getExt());
                }
                break;
            }
            default: {
                exporter = new JRPdfExporter();
                if (response != null) {
                    response.setContentType("application/pdf");
                    response.addHeader("Content-disposition", "inline; filename=" + onlyFilename + DocTypeEnum.PDF.getExt());
                }
                break;
            }
        }

        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

        return exporter;
    }

    private static String getOnlyReportNameFormat(final String reportName) {
        String fileFormat = reportName;
        if (StringUtils.isEmpty(fileFormat)) {
            fileFormat = "reports";

        } else {

            // For filename reports (jasperreports)
            //fileFormat = fileFormat.replace(' ', '-');

            // For windows -> filename
            fileFormat = fileFormat.replace('\\', '-');
            fileFormat = fileFormat.replace('/', '-');
            fileFormat = fileFormat.replace(':', '-');
            fileFormat = fileFormat.replace('*', '-');
            fileFormat = fileFormat.replace('?', '-');
            fileFormat = fileFormat.replace('\"', '-');
            fileFormat = fileFormat.replace('<', '-');
            fileFormat = fileFormat.replace('>', '-');
            fileFormat = fileFormat.replace('|', '-');
        }
        return fileFormat;
    }

}
