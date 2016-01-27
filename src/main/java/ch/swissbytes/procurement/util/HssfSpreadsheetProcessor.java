package ch.swissbytes.procurement.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.io.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Christian on 20/07/2015.
 */

public class HssfSpreadsheetProcessor implements Serializable {

    private static final Logger log = Logger.getLogger(HssfSpreadsheetProcessor.class.getName());

    //Create blank workbook

    private HSSFWorkbook workbookHss;

    //Create a blank sheet

    private HSSFSheet spreadsheetHss;

    //Create row object
    private HSSFRow rowHss;

    private String passwordSheet = System.getProperty("fqmes.excel.sheet.password");

    private boolean createdDirectories;

    public void createWorkbook() {
        workbookHss = new HSSFWorkbook();
    }

    public void createSpreadsheetHssf(String name) {
        spreadsheetHss = workbookHss.createSheet(name);
        spreadsheetHss.protectSheet(passwordSheet);
    }

    public void createSpreadsheetWithoutPassword(String name){
        spreadsheetHss = workbookHss.createSheet(name);
    }

    public void configureWithColumn(int indexColumn, int numberOfCharacters) {
        spreadsheetHss.setColumnWidth(indexColumn, numberOfCharacters);
    }

    public void createRow(int rowNo) {
        rowHss = spreadsheetHss.createRow((short) rowNo);
    }

    public void writeStringValue(int colNo, String value) {
        rowHss.createCell(colNo).setCellValue(value);
    }

    public void writeStringBoldValue(int colNo, String value) {
        HSSFCellStyle style = workbookHss.createCellStyle();
        HSSFFont font = workbookHss.createFont();
        font.setBold(true);
        style.setFont(font);
        Cell cell0 = rowHss.createCell(colNo);
        cell0.setCellValue(value);
        cell0.setCellStyle(style);
    }
    public void writeRichStringValue(int colNo, String value, Map<String,Integer> indexRichString) {
        Cell cell0 = rowHss.createCell(colNo);
        HSSFFont font = workbookHss.createFont();
        font.setBold(true);

        HSSFRichTextString richString = new HSSFRichTextString(value);
        richString.applyFont(indexRichString.get("poIni").intValue(), indexRichString.get("poEnd").intValue(), font);
        richString.applyFont(indexRichString.get("titleIni").intValue(), indexRichString.get("titleEnd").intValue(), font);
        richString.applyFont(indexRichString.get("dateIni").intValue(), indexRichString.get("dateEnd").intValue(), font);
        richString.applyFont(indexRichString.get("incoIni").intValue(), indexRichString.get("incoEnd").intValue(), font);
        richString.applyFont(indexRichString.get("supIni").intValue(), indexRichString.get("supEnd").intValue(), font);
        richString.applyFont(indexRichString.get("statusIni").intValue(), indexRichString.get("statusEnd").intValue(), font);
        richString.applyFont(indexRichString.get("rfeIni").intValue(), indexRichString.get("rfeEnd").intValue(), font);
        cell0.setCellValue(richString);
    }

    public void writeDoubleValue(int colNo, Double value) {
        rowHss.createCell(colNo).setCellValue(value == null ? "" : value.toString());
    }

    public void doSaveWorkBook(final String path, final String fileName) throws Exception {
        FileOutputStream out = null;
        File file = createDirectoryFiles(path);
        File newFile = new File(file.getAbsolutePath() + File.separator + fileName);
        try {
            if (newFile.createNewFile()) {
                System.out.println("created file");
            }
            out = new FileOutputStream(newFile);
            workbookHss.write(out);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("It is not able to either the directory ["+file.getAbsolutePath()+"] ");
        } finally {
            try {
                log.info("closing fileOutputStream out");
                if (out != null) {
                    out.close();
                    log.info("closed out");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createDirectoryFiles(String path) {
        File theDir = new File(path);
        if (!theDir.exists()) {
            try {
                theDir.mkdirs();
            } catch (SecurityException se) {

            }
        }
        return theDir;
    }

    public ByteArrayOutputStream saveWorkBook() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbookHss.write(outputStream);
            return outputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public InputStream getContentSheet() {
        ByteArrayOutputStream output = saveWorkBook();
        if (output != null) {
            return new ByteArrayInputStream(output.toByteArray());
        }
        return null;
    }

}
