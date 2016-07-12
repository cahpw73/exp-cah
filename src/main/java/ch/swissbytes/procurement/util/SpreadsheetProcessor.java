package ch.swissbytes.procurement.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Christian on 20/07/2015.
 */

public class SpreadsheetProcessor implements Serializable {

    private static final Logger log = Logger.getLogger(SpreadsheetProcessor.class.getName());

    //Create blank workbook
    private XSSFWorkbook workbook;

    //Create a blank sheet
    private XSSFSheet spreadsheet;

    //Create row object
    private XSSFRow row;

    private String passwordSheet = System.getProperty("fqmes.excel.sheet.password");

    private boolean createdDirectories;

    public void createWorkbook() {
        workbook = new XSSFWorkbook();
    }

    public void createSpreadsheet(String name) {
        spreadsheet = workbook.createSheet(name);
        spreadsheet.protectSheet(passwordSheet);
    }

    public void createSpreadsheetWithoutPassword(String name){
        spreadsheet = workbook.createSheet(name);
    }

    public void configureWithColumn(int indexColumn, int numberOfCharacters) {
        spreadsheet.setColumnWidth(indexColumn, numberOfCharacters);
    }

    public void createRow(int rowNo) {
        row = spreadsheet.createRow((short) rowNo);
    }

    public void createRowWithHeight(int rowNo,short height) {
        row = spreadsheet.createRow((short) rowNo);
        row.setHeight(height);
    }

    public void writeStringValue(int colNo, String value) {
        row.createCell(colNo).setCellValue(value);
    }

    public void writeStringValueWithStyles(int colNo, String value,List<XSSFCellStyle> styles){
        XSSFCell cell = row.createCell(colNo);
        cell.setCellValue(value);
        for(XSSFCellStyle style : styles){
            cell.setCellStyle(style);
        }
    }

    public XSSFCellStyle getStyleAlignTop(){
        XSSFCellStyle style1 = workbook.createCellStyle();
        style1.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
        return style1;
    }
    public XSSFCellStyle getStyleVerticalJustify(){
        XSSFCellStyle style1 = workbook.createCellStyle();
        style1.setVerticalAlignment(XSSFCellStyle.VERTICAL_JUSTIFY);
        return style1;
    }

    public void writeStringBoldValue(int colNo, String value) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        Cell cell0 = row.createCell(colNo);
        cell0.setCellValue(value);
        cell0.setCellStyle(style);
    }

    public void writeRichStringValue(int colNo, String value, Map<String,Integer> indexRichString) {
        Cell cell0 = row.createCell(colNo);
        XSSFFont font = workbook.createFont();
        font.setBold(true);

        XSSFRichTextString richString = new XSSFRichTextString(value);
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
        row.createCell(colNo).setCellValue(value == null ? "" : value.toString());
    }

    public void doSaveWorkBook(final String path, final String fileName) throws IOException {
        FileOutputStream out = null;
        File file = createDirectoryFiles(path);
        File newFile = new File(file.getAbsolutePath() + File.separator + fileName);
        try {
            if (newFile.createNewFile()) {
                System.out.println("created file");
            }
            out = new FileOutputStream(newFile);
            workbook.write(out);
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
            workbook.write(outputStream);
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
