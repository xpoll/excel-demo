package cn.blmdz.excel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Data;



/**
 * <pre>
 * HSSFWorkbook
 * EXCEL2003 -> *.xls -> 65535行256列
 * 一般不会OOM
 * 
 * XSSFWorkbook
 * EXCEL2007+ -> *.xlsx -> 1048576行16384列
 * 数据大会OOM
 * 
 * SXSSFWorkbook
 * EXCEL2007+ -> *.xlsx -> 1048576行16384列
 * new SXSSFWorkbook(DEFAULT_WINDOW_SIZE) 根据DEFAULT_WINDOW_SIZE大小 超出条数将原来的持久化到硬盘解决OOM问题 默认100(基本默认即可)
 */
public class ExcelPoi {
    
    public enum ExcelType {
        HSSFWorkbook("EXCEL2003; 65535行256列; 一般不会OOM;"),
        XSSFWorkbook("EXCEL2007; 1048576行16384列; 数据大会OOM;"),
        SXSSFWorkbook("EXCEL2007; 1048576行16384列; 根据DEFAULT_WINDOW_SIZE大小 超出条数将原来的持久化到硬盘解决OOM问题 默认100(基本默认即可);"),
        ;
        ExcelType(String description) {}
    }
    
    @Data
    public static class ExcelModel {
        private OutputStream stream;
        private ExcelType excelType;
        private Boolean append = false;
        private List<String> columnCode;
        private List<String> columnDescription;
        private List<Map<String, String>> data;
    }

    @Data
    @AllArgsConstructor
    public static class ColumnDetail {
    	private List<String> columnCode;
        private List<String> columnDescription;
    }
    
    public static void excelCreate(ExcelModel model) throws IOException {
        Workbook workbook = null;
        switch (model.getExcelType()) {
        case HSSFWorkbook:
            workbook = new HSSFWorkbook();
            break;
        case SXSSFWorkbook:
            workbook = new SXSSFWorkbook();
            break;
        case XSSFWorkbook:
            workbook = new XSSFWorkbook();
            break;
        }
        

        int s = model.getData().size() / 1000000 + (model.getData().size() % 1000000 > 0 ? 1 : 0);
        if (s == 0) workbook.createSheet("sheet1");
        else
	        for (int i = 0; i < s; i++) {
	            workbook.createSheet("sheet" + (i + 1));
	        }
        String tmp = null;
        for (int i = 0; i <= model.getData().size(); i++) {
            
            Sheet sheet = workbook.getSheetAt(i / 1000000);
            Row row = sheet.createRow(i % 1000000);

            for (int j = 0; j < model.getColumnDescription().size(); j++) {
                if(i == 0) {
                    // 首行
                    tmp = model.getColumnDescription().get(j);
                } else {
                    // 数据
                	tmp = model.getData().get(i - 1).get(model.getColumnCode().get(j));
                }
//              row.createCell(j).setCellValue(tmp);
                CellUtil.createCell(row, j, tmp);
            }
        }

        workbook.write(model.getStream());
        if (workbook instanceof SXSSFWorkbook)((SXSSFWorkbook) workbook).dispose();
        if (workbook != null) workbook.close();
    }
    public static List<List<String>> excelRead(String filePath, ExcelType type, Integer startRow, Integer startCell) throws IOException {
        return excelRead(new FileInputStream(new File(filePath)), type, startRow, startCell);
    }
    
    public static List<List<String>> excelRead(InputStream is, ExcelType type, Integer startRow, Integer startCell) throws IOException {
        
        Workbook workbook = null;

        switch (type) {
        case HSSFWorkbook:
            workbook = new HSSFWorkbook(is);
        case SXSSFWorkbook:
            workbook = new SXSSFWorkbook(new XSSFWorkbook(is));
        case XSSFWorkbook:
            workbook = new XSSFWorkbook(is);
        }
        List<List<String>> rowData = Lists.newArrayList();
        List<String> cellData = Lists.newArrayList();
        
        // Sheet
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // Row
            for (int j = startRow; j < sheet.getPhysicalNumberOfRows(); j++) {
                cellData = Lists.newArrayList();
                Row row = sheet.getRow(j);
                // Cell
                for (int k = startCell; k < row.getPhysicalNumberOfCells(); k++) {
                    Cell cell = row.getCell(k);
                    String value = null;
                    if (cell != null) {
                    	switch (cell.getCellTypeEnum()) {
                    	case _NONE:
                    	case STRING:
                    		value = cell.getStringCellValue();
                    		break;
                    	case NUMERIC:
                    		if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    			String format = null;
                    			if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd hh:mm:ss")) {
                    				format = "yyyy-MM-dd hh:mm:ss";
            					} else if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd")) {
                    				format = "yyyy-MM-dd hh:mm:ss";
            					} else if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("yyyy/MM/dd")) {
                    				format = "yyyy/MM/dd";
            					} else {
            						format = "yyyy-MM-dd hh:mm:ss";
            					}
                    			value = new SimpleDateFormat(format).format(cell.getDateCellValue());
                    		} else {
                    			NumberFormat nf = NumberFormat.getInstance();
                    			value = nf.format(cell.getNumericCellValue()).replace(",", "");
                    		}
                    		break;
                    	case FORMULA:
                    		value = cell.getCellFormula();
                    		break;
                    	case BOOLEAN:
                    		value = String.valueOf(cell.getBooleanCellValue());
                    		break;
                    	case ERROR:
                    	case BLANK:
                    		value = "";
                    		break;
                    	}
                    } else {
                    	value = "";
                    }
                    cellData.add(value);
                }
                rowData.add(cellData);
            }
        }
        
        if (is != null) is.close();
        if (workbook instanceof SXSSFWorkbook)((SXSSFWorkbook) workbook).dispose();
        if (workbook != null) workbook.close();
        return rowData;
    }

}
