package com.hnix.sd.work.software.manage.service;


import com.hnix.sd.common.excel.dto.ExcelField;
import com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import com.hnix.sd.common.excel.util.ExcelFormat.CELL;
import com.hnix.sd.work.software.manage.dto.SoftwareInfoDto;
import com.hnix.sd.work.software.manage.dto.SearchSoftwareDto;
import com.hnix.sd.work.software.manage.dto.SoftwareExcelDto;
import com.hnix.sd.common.excel.util.WorkCellStyle;
import com.hnix.sd.common.excel.util.WorkSheetLayout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.hnix.sd.work.software.manage.dao.SoftwareDao;
import com.hnix.sd.work.software.manage.dto.SoftwareDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class SoftwareExcelService {
  private final SoftwareService softwareService;
  private final SoftwareDao softwareDao;

	public SXSSFWorkbook exportSoftwareExcel(SearchSoftwareDto searchSoftwareDto) {
        List<SoftwareInfoDto> softwareList = softwareService.searchSoftwareInfo(searchSoftwareDto);
        List<SoftwareExcelDto> excelDataList = new ArrayList<>();

        for (SoftwareInfoDto softwareInfoDto : softwareList) {
            SoftwareExcelDto dto = new SoftwareExcelDto(
                softwareInfoDto.getSwCode(),
                softwareInfoDto.getSwName(),
                softwareInfoDto.getSwDesc()
            );
            excelDataList.add(dto);
        }

        List<ExcelField> fields = new ArrayList<>();
        fields.add(new ExcelField("swCode", "소프트웨어 코드", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("swName", "소프트웨어 이름", ALIGN.LEFT, 10000));
        fields.add(new ExcelField("swDesc", "설명", ALIGN.LEFT, 12000));

        return createSoftwareExcelFile(excelDataList, fields);
    }

    private SXSSFWorkbook createSoftwareExcelFile(List<SoftwareExcelDto> results, List<ExcelField> fields) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = WorkSheetLayout.createSheetLayout(workbook, "소프트웨어 관리", fields);

        Font bodyFont = WorkCellStyle.setCellFont(workbook.createFont(), 230, false);
        CellStyle bodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.CENTER, true, false, CELL.BODY);
        CellStyle alignBodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.LEFT, true, false, CELL.BODY_ALIGN);

        int startRowIndex = 4;

        for (SoftwareExcelDto dto : results) {
            SXSSFRow row = sheet.createRow(startRowIndex++);
            List<String> rowData = getSoftwareExcelFieldValues(dto);

            for (int i = 0; i < fields.size(); i++) {
                ExcelField field = fields.get(i);
                String val = rowData.get(i);
                SXSSFCell cell = row.createCell(i + 1);
                cell.setCellValue(val == null ? "" : val);
                cell.setCellStyle(field.getAlign() == ALIGN.CENTER ? bodyCellStyle : alignBodyCellStyle);
            }
        }
        return workbook;
    }

    private List<String> getSoftwareExcelFieldValues(SoftwareExcelDto dto) {
        return List.of(
          dto.getSwCode() == null ? "" : dto.getSwCode(),
          dto.getSwName() == null ? "" : dto.getSwName(),
          dto.getSwDesc() == null ? "" : dto.getSwDesc()
        );
    }

    public List<SoftwareExcelDto> parseSoftwareExcel(MultipartFile file) {
        List<SoftwareExcelDto> dtoList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowIndex = 0;

            while (rows.hasNext()) {
                Row row = rows.next();

                if (rowIndex++ < 2) continue;

                SoftwareExcelDto dto = new SoftwareExcelDto();

                dto.setSwCode(getCellValue(row, 1));
                dto.setSwName(getCellValue(row, 2));
                dto.setSwDesc(getCellValue(row, 3));

                dto.setErrorMsg(dto.validate());

                SoftwareDto softwareOpt = softwareDao.findBySwCode(dto.getSwCode());

                if (softwareOpt != null) {
                    dto.setNewRow(false);
                    dto.setUpdateRow(true);
                } else {
                    dto.setNewRow(true);
                    dto.setUpdateRow(false);
                }
                dtoList.add(dto);
            }
        } catch (Exception e) {
            log.error("엑셀 파싱 중 오류", e);
            throw new RuntimeException("엑셀 파싱 실패: " + e.getMessage(), e);
        }

        return dtoList;
    }   

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /** 엑셀 업로드 후 DB 저장 */
    public List<String> storeSoftwareList(List<SoftwareExcelDto> excelDtoList) {
      List<String> registerList = new ArrayList<>();
        for (SoftwareExcelDto excel : excelDtoList) {
            try {
                if (excel.isNewRow()) {
                    SoftwareDto software = new SoftwareDto();

                    software.setSwCode(excel.getSwCode());
                    software.setSwName(excel.getSwName());
                    software.setSwDesc(excel.getSwDesc());
                    
                    softwareDao.insertSoftware(software);
                    
                } else if (excel.isUpdateRow()) {
                    SoftwareInfoDto updateDto = new SoftwareInfoDto();
                    updateDto.setSwCode(excel.getSwCode());
                    updateDto.setSwName(excel.getSwName());
                    updateDto.setSwDesc(excel.getSwDesc());
                    
                    softwareService.storeSoftwareInfo(updateDto);
                }
                
            } catch (Exception e) {
                log.error("사용자 저장 실패: {}", "aaa", e);
                registerList.add(excel.getSwCode());
            }
        }
        
        return registerList;
    }
}
