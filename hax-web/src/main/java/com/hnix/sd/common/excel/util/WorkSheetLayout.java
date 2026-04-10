package com.hnix.sd.common.excel.util;

import com.hnix.sd.common.excel.dto.ExcelField;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

@Slf4j
public class WorkSheetLayout {

    public static SXSSFSheet createSheetLayout(SXSSFWorkbook workbook, final String sheetTitle, List<ExcelField> fields) {
        SXSSFSheet sheet = workbook.createSheet(sheetTitle);

        for (int i = 0; i < fields.size(); i++) {
            sheet.setColumnWidth(i + 1, fields.get(i).getWidth());
        }

        /* Excel title cell */
        CellStyle titleStyle = workbook.createCellStyle();

        Font titleFont = workbook.createFont();
        titleFont.setFontHeight((short) 360);
        titleFont.setBold(true);

        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setFont(titleFont);

        SXSSFRow titleRow = sheet.createRow(1);
        SXSSFCell titleCell = titleRow.createCell(1);

        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue(sheetTitle);

        Font headerFont = WorkCellStyle.setCellFont(workbook.createFont(), 210, true);
        CellStyle headerStyle = WorkCellStyle.setCellStyle(
                workbook.createCellStyle(),
                headerFont,
                ExcelFormat.ALIGN.CENTER, true, true,
                ExcelFormat.CELL.HEADER);

        SXSSFRow headerRow = sheet.createRow(3);
        headerRow.setHeightInPoints((2 * sheet.getDefaultRowHeightInPoints()));

        for (int i = 0; i < fields.size(); i++) {
            SXSSFCell headerCell = headerRow.createCell(i + 1);
            headerCell.setCellValue(fields.get(i).getTitle());
            headerCell.setCellStyle(headerStyle);
        }

        return sheet;
    }


    public static XSSFSheet createSheetLayout2(XSSFWorkbook workbook, final String sheetTitle, List<ExcelField> fields) {
        XSSFSheet sheet = workbook.createSheet(sheetTitle);

        for (int i = 0; i < fields.size(); i++) {
            sheet.setColumnWidth(i + 1, fields.get(i).getWidth());
        }

        /* Excel title cell */
        CellStyle titleStyle = workbook.createCellStyle();

        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 18);  // 360/20 정도로 맞춤
        titleFont.setBold(true);

        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setFont(titleFont);

        XSSFRow titleRow = sheet.createRow(1);
        XSSFCell titleCell = titleRow.createCell(1);

        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue(sheetTitle);

        Font headerFont = WorkCellStyle.setCellFont(workbook.createFont(), 210, true);
        CellStyle headerStyle = WorkCellStyle.setCellStyle(
                workbook.createCellStyle(),
                headerFont,
                ExcelFormat.ALIGN.CENTER, true, true,
                ExcelFormat.CELL.HEADER);

        XSSFRow headerRow = sheet.createRow(3);
        headerRow.setHeightInPoints((2 * sheet.getDefaultRowHeightInPoints()));

        for (int i = 0; i < fields.size(); i++) {
            XSSFCell headerCell = headerRow.createCell(i + 1);
            headerCell.setCellValue(fields.get(i).getTitle());
            headerCell.setCellStyle(headerStyle);
        }

        return sheet;
    }

}
