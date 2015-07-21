package ch.swissbytes.procurement.util;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Christian on 20/07/2015.
 */
public class testApachePOI implements Serializable {


   /* public static void main(String[] args) throws Exception
    {
        //Create blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        //Create a blank sheet
        XSSFSheet spreadsheet = workbook.createSheet(
                " Employee Info ");
        //Create row object
        XSSFRow row;
        //This data needs to be written (Object[])
        Map<String, Object[]> empinfo = new TreeMap<>();
        empinfo.put( "1", new Object[] {"EMP ID", "EMP NAME", "DESIGNATION" });
        empinfo.put( "2", new Object[] {"tp01", "Gopal", "Technical Manager" });
        empinfo.put( "3", new Object[] {"tp02", "Manisha", "Proof Reader" });
        empinfo.put( "4", new Object[] {"tp03", "Masthan", "Technical Writer" });
        empinfo.put( "5", new Object[] {"tp04", "Satish", "Technical Writer" });
        empinfo.put( "6", new Object[] {"tp05", "Krishna", "Technical Writer" });
        //Iterate over data and write to sheet
        Set< String > keyid = empinfo.keySet();
        int rowid = 0;
        for (String key : keyid)
        {
            row = spreadsheet.createRow(rowid++);
            Object [] objectArr = empinfo.get(key);
            int cellid = 0;
            for (Object obj : objectArr)
            {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);
            }
        }
        //Write the workbook in file system
        FileOutputStream out = new FileOutputStream(
                new File("D:\\Writesheet.xlsx"));
        workbook.write(out);
        out.close();
        System.out.println(
                "Writesheet.xlsx written successfully" );
    }*/

   /* static XSSFRow row;
    public static void main(String[] args) throws Exception
    {
        FileInputStream fis = new FileInputStream(
                new File("D:\\typesofcells.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = spreadsheet.iterator();
        while (rowIterator.hasNext())
        {

            row = (XSSFRow) rowIterator.next();
            Iterator < Cell > cellIterator = row.cellIterator();
            while ( cellIterator.hasNext())
            {
                Cell cell = cellIterator.next();
                switch (cell.getCellType())
                {
                    case Cell.CELL_TYPE_NUMERIC:
                        System.out.print(
                                cell.getNumericCellValue() + " \t\t " );
                        break;
                    case Cell.CELL_TYPE_STRING:
                        System.out.print(
                                cell.getStringCellValue() + " \t\t " );
                        break;
                }
            }
            System.out.println();
        }
        fis.close();
    }*/

    public static void main(String[] args)throws Exception
    {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("cell types");
        XSSFRow row = spreadsheet.createRow((short) 0);
        row.createCell(0).setCellValue("Type of Cell");
        row.createCell(1).setCellValue("cell value");
        row.createCell(2).setCellValue(true);
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        row.createCell(3).setCellValue(new Date());
        BigDecimal bigDecimal = new BigDecimal(BigInteger.TEN);
        row.createCell(4).setCellValue(bigDecimal.doubleValue());
        row.createCell(5).setCellValue(sdf.format(new Date()));


        FileOutputStream out = new FileOutputStream(new File("D:\\typesofcells.xlsx"));
        workbook.write(out);
        out.close();
        System.out.println(
                "typesofcells.xlsx written successfully");
    }
}
