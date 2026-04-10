package com.hnix.sd.common.excel.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.text.SimpleDateFormat;

import com.hnix.sd.common.excel.dao.ExcelDao;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import lombok.RequiredArgsConstructor;

import com.hnix.sd.common.excel.dto.ExcelField;
import com.hnix.sd.common.excel.dto.ReportSubcodeExcelParams;
import com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import com.hnix.sd.common.excel.util.ExcelFormat.CELL;
import com.hnix.sd.common.excel.util.WorkCellStyle;
import com.hnix.sd.common.excel.util.WorkExcelCommon;
import com.hnix.sd.common.excel.util.WorkSheetLayout;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportSubcodeExcelExportService implements WorkExcelCommon{
  private final ExcelDao excelDao;

  public XSSFWorkbook reportSubcodeExcelExport(ReportSubcodeExcelParams params) throws IOException {
    InputStream fis = new ClassPathResource("template/SubCodeReport.xlsx").getInputStream();
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    fis.close();

    Map<String, Object> queryParams = Map.of(
            "contractYear", params.getContractYear(),
            "customerCode", params.getCustomerCode(),
            "startDate", params.getStartDate(),
            "endDate", params.getEndDate(),
            "partnerContractCd", params.getPartnerContractCd() != null ? params.getPartnerContractCd() : ""
    );

    List<Map<String, Object>> reportData = excelDao.getReportSubcode(queryParams);

    List<Map<String, Object>> workData = excelDao.getWorkList(queryParams);

    List<Map<String, Object>> partnerData = excelDao.getReportPartner(queryParams);

    LinkedHashMap<String, List<Map<String, Object>>> workDataBySubCode = new LinkedHashMap<>();

    // 1단계: reportData에서 나온 순서대로 sub_cd 키 생성 (순서 보장)
    if (reportData != null && !reportData.isEmpty()) {
        for (Map<String, Object> report : reportData) {
            String subCd = report.get("sub_cd") != null ? (String) report.get("sub_cd") : "Unknown";
            workDataBySubCode.putIfAbsent(subCd, new ArrayList<>());
        }
    }

    // 2단계: workData를 sub_cd별로 분류
    if (workData != null && !workData.isEmpty()) {
        for (Map<String, Object> work : workData) {
            String subCd = work.get("sub_cd") != null ? (String) work.get("sub_cd") : "Unknown";
            workDataBySubCode.computeIfAbsent(subCd, k -> new ArrayList<>()).add(work);
        }
    }

      // 2단계: workData를 sub_cd별로 분류
      if (partnerData != null && !partnerData.isEmpty()) {
          for (Map<String, Object> partner : partnerData) {
              String subCd = partner.get("sub_cd") != null ? (String) partner.get("sub_cd") : "Unknown";
              workDataBySubCode.computeIfAbsent(subCd, k -> new ArrayList<>()).add(partner);
          }
      }

    XSSFSheet sheet = workbook.getSheetAt(0); //첫번째 시트
    XSSFSheet sheet2 = workbook.getSheetAt(1); //두번째 시트
    XSSFSheet templateSheet = workbook.getSheetAt(2); // 템플릿으로 사용할 3번째 시트

    // 제목/기간 셋팅 (1~2행)
    setCellValueSafe(sheet, 0, 0, "▣ " + params.getContractYear() + "년 S/W별 기술지원 현황");
    setCellValueSafe(sheet, 1, 0, String.format("유지보수 기간: %s ~ %s", params.getStartDate(), params.getEndDate()));

    setCellValueSafe(sheet2, 0, 0, "▣ " + params.getContractYear() + "년 S/W별 기술지원 현황_협력사별 집계("+params.getCustomerCodeText()+")");
    setCellValueSafe(sheet2, 1, 0, String.format("유지보수 기간: %s ~ %s", params.getStartDate(), params.getEndDate()));

    int dataStartRow = 6; // 7행부터 데이터 시작 (0-based index)
    
    int lastRowIndex = sheet.getLastRowNum(); // 마지막 행 인덱스(첫번째)
    int lastRowIndex2 = sheet2.getLastRowNum(); // 마지막 행 인덱스(두번째)
    int rowsToAdd = reportData != null ? reportData.size() : 0; // 추가할 데이터 행 수
    int rowsToAdd2 = partnerData != null ? partnerData.size() : 0;

    XSSFRow templateRow = sheet.getRow(dataStartRow);
    XSSFRow templateRow2 = sheet2.getRow(dataStartRow);

    // 1. 합계 행을 아래로 이동 (데이터 공간 확보)
    if (rowsToAdd > 1) {
      sheet.shiftRows(dataStartRow, lastRowIndex, rowsToAdd - 1, true, true);
    }
    if (rowsToAdd2 > 1) {
      sheet2.shiftRows(dataStartRow, lastRowIndex2, rowsToAdd2 - 1, true, true);
    }
    
    
    // 2. 새 행 추가 및 데이터 입력
    for (int i = 0; i < reportData.size(); i++) {
      Map<String, Object> rowMap = reportData.get(i);
      
      // 스타일과 수식을 복사한 새 행 생성
      XSSFRow newRow = addRowWithStyleAndFormula(sheet, workbook, dataStartRow + i, templateRow);

      // 데이터 입력
      setCellValueSafe(newRow, 0, rowMap.get("sub_cd"));
      setCellValueSafe(newRow, 1, rowMap.get("support_company_nm"));
      setCellValueSafe(newRow, 2, rowMap.get("sw_nm"));

      // 서비스 항목별 합계 (소계 열 3은 E~L 열의 합)
      XSSFCell sumTotalCell = newRow.getCell(3);
      if (sumTotalCell == null) sumTotalCell = newRow.createCell(3);
      int excelRowNum = dataStartRow + i + 1; // 1-based 행 번호
      sumTotalCell.setCellFormula(String.format("SUM(E%d:L%d)", excelRowNum, excelRowNum));

      // 서비스 항목별 합계
      setCellValueSafe(newRow, 4, rowMap.get("sum_02"));
      setCellValueSafe(newRow, 5, rowMap.get("sum_01"));
      setCellValueSafe(newRow, 6, rowMap.get("sum_06"));
      setCellValueSafe(newRow, 7, rowMap.get("sum_07"));
      setCellValueSafe(newRow, 8, rowMap.get("sum_08"));
      setCellValueSafe(newRow, 9, rowMap.get("sum_04"));
      setCellValueSafe(newRow, 10, rowMap.get("sum_05"));
      setCellValueSafe(newRow, 11, rowMap.get("sum_03"));

      XSSFCell methodAllCell = newRow.getCell(12);
      if (methodAllCell == null) methodAllCell = newRow.createCell(12);
      methodAllCell.setCellFormula(String.format("SUM(N%d:S%d)", excelRowNum, excelRowNum));

      // 지원매체별 합계
      setCellValueSafe(newRow, 13, rowMap.get("method_06"));
      setCellValueSafe(newRow, 14, rowMap.get("method_03"));
      setCellValueSafe(newRow, 15, rowMap.get("method_05"));
      setCellValueSafe(newRow, 16, rowMap.get("method_01"));
      setCellValueSafe(newRow, 17, rowMap.get("method_04"));
      setCellValueSafe(newRow, 18, rowMap.get("method_02"));
      setCellValueSafe(newRow, 19, rowMap.get("remark"));
    }

    // 2. 새 행 추가 및 데이터 입력(두번째)
    for (int i = 0; i < partnerData.size(); i++) {
      Map<String, Object> rowMap = partnerData.get(i);
      
      // 스타일과 수식을 복사한 새 행 생성
      XSSFRow newRow = addRowWithStyleAndFormula(sheet2, workbook, dataStartRow + i, templateRow2);

      // 데이터 입력
      setCellValueSafe(newRow, 0, i + 1); 
      setCellValueSafe(newRow, 1, rowMap.get("support_company_nm"));
      setCellValueSafe(newRow, 2, rowMap.get("sw_nm_full"));

      // 서비스 항목별 합계 (소계 열 3은 E~L 열의 합)
      XSSFCell sumTotalCell = newRow.getCell(3);
      if (sumTotalCell == null) sumTotalCell = newRow.createCell(3);
      int excelRowNum = dataStartRow + i + 1; // 1-based 행 번호
      sumTotalCell.setCellFormula(String.format("SUM(E%d:L%d)", excelRowNum, excelRowNum));

      // 서비스 항목별 합계
      setCellValueSafe(newRow, 4, rowMap.get("sum_02"));
      setCellValueSafe(newRow, 5, rowMap.get("sum_01"));
      setCellValueSafe(newRow, 6, rowMap.get("sum_06"));
      setCellValueSafe(newRow, 7, rowMap.get("sum_07"));
      setCellValueSafe(newRow, 8, rowMap.get("sum_08"));
      setCellValueSafe(newRow, 9, rowMap.get("sum_04"));
      setCellValueSafe(newRow, 10, rowMap.get("sum_05"));
      setCellValueSafe(newRow, 11, rowMap.get("sum_03"));

      XSSFCell methodAllCell = newRow.getCell(12);
      if (methodAllCell == null) methodAllCell = newRow.createCell(12);
      methodAllCell.setCellFormula(String.format("SUM(N%d:S%d)", excelRowNum, excelRowNum));

      // 지원매체별 합계
      setCellValueSafe(newRow, 13, rowMap.get("method_06"));
      setCellValueSafe(newRow, 14, rowMap.get("method_03"));
      setCellValueSafe(newRow, 15, rowMap.get("method_05"));
      setCellValueSafe(newRow, 16, rowMap.get("method_01"));
      setCellValueSafe(newRow, 17, rowMap.get("method_04"));
      setCellValueSafe(newRow, 18, rowMap.get("method_02"));

      setCellValueSafe(newRow, 19, rowMap.get("remark"));
    }
    if (workDataBySubCode.isEmpty()) {
      setCellValueSafe(templateSheet, 0, 0, "▣ S/W 유지보수 서비스 상세내역");
      setCellValueSafe(templateSheet, 1, 0, String.format("유지보수 기간: %s ~ %s", params.getStartDate(), params.getEndDate()));
    } else {
      int sheetIndex = 2; // 템플릿 시트의 인덱스

      XSSFSheet backupTemplate = workbook.cloneSheet(sheetIndex);
      int backupIndex = workbook.getSheetIndex(backupTemplate);
      workbook.setSheetName(backupIndex, "TEMPLATE_BACKUP");
      workbook.setSheetHidden(backupIndex, true); // 숨김 처리

      boolean isFirstSubCodeSheet = true;
        
      for (Map.Entry<String, List<Map<String, Object>>> entry : workDataBySubCode.entrySet()) {
        String currentSubCd = entry.getKey();
        List<Map<String, Object>> subCodeWorkList = entry.getValue();
        
        XSSFSheet subCodeSheet;
        
        if (isFirstSubCodeSheet) {
            // 첫 번째는 기존 3번째 시트 사용
            subCodeSheet = templateSheet;
            workbook.setSheetName(sheetIndex, currentSubCd);
            isFirstSubCodeSheet = false;
        } else {
            // 나머지는 템플릿 시트 복사
            subCodeSheet = workbook.cloneSheet(backupIndex);
            int newSheetIndex = workbook.getSheetIndex(subCodeSheet);
            workbook.setSheetName(newSheetIndex, currentSubCd);
        }
        
        // SubCode별 시트에 데이터 입력
        fillSubCodeSheet(workbook, subCodeSheet, params, subCodeWorkList, reportData);
      }
    }

    int headerSumRow = 5; // 6행 (헤더 아래 합계)
    int footerSumRow = dataStartRow + rowsToAdd + 1; // 데이터 끝 다음 행 (이동된 합계 행)
    int firstDataRow = dataStartRow + 1; // 엑셀 행 번호 (1-based)
    int lastDataRow = dataStartRow + rowsToAdd; // 엑셀 행 번호 (1-based)
    int lastDataRow2 = dataStartRow + rowsToAdd2;
    int footerSumRow2 = dataStartRow + rowsToAdd2 + 1;
    
    int[] sumCols = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
    
    // 헤더 아래 합계 행 수식 재설정
    XSSFRow headerSum = sheet.getRow(headerSumRow);
    if (headerSum != null) {
      for (int col : sumCols) {
        XSSFCell sumCell = headerSum.getCell(col);
        if (sumCell == null) {
          sumCell = headerSum.createCell(col);
        }
        String colLetter = CellReference.convertNumToColString(col);
        String newFormula = String.format("SUM(%s%d:%s%d)", colLetter, firstDataRow, colLetter, lastDataRow);
        sumCell.setCellFormula(newFormula);
      }
    }
    XSSFRow headerSum2 = sheet2.getRow(headerSumRow);
    if (headerSum2 != null) {
      for (int col : sumCols) {
        XSSFCell sumCell = headerSum2.getCell(col);
        if (sumCell == null) {
          sumCell = headerSum2.createCell(col);
        }
        String colLetter = CellReference.convertNumToColString(col);
        String newFormula = String.format("SUM(%s%d:%s%d)", colLetter, firstDataRow, colLetter, lastDataRow2);
        sumCell.setCellFormula(newFormula);
      }
    }

    XSSFRow footerSum = sheet.getRow(footerSumRow);
    if (footerSum != null) {
      for (int col : sumCols) {
        XSSFCell sumCell = footerSum.getCell(col);
        if (sumCell == null) {
          sumCell = footerSum.createCell(col);
        }
        String colLetter = CellReference.convertNumToColString(col);
        String newFormula = String.format("SUM(%s%d:%s%d)", colLetter, firstDataRow, colLetter, lastDataRow);
        sumCell.setCellFormula(newFormula);
      }
    }
    XSSFRow footerSum2 = sheet2.getRow(footerSumRow2);
    if (footerSum2 != null) {
      for (int col : sumCols) {
        XSSFCell sumCell = footerSum2.getCell(col);
        if (sumCell == null) {
          sumCell = footerSum2.createCell(col);
        }
        String colLetter = CellReference.convertNumToColString(col);
        String newFormula = String.format("SUM(%s%d:%s%d)", colLetter, firstDataRow, colLetter, lastDataRow2);
        sumCell.setCellFormula(newFormula);
      }
    }  
    sheet.setForceFormulaRecalculation(true);    
    return workbook;
  }
  //3번째 시트
  private void fillSubCodeSheet(XSSFWorkbook workbook, XSSFSheet sheet, 
    ReportSubcodeExcelParams params, 
    List<Map<String, Object>> workList,
    List<Map<String, Object>> reportData) {

    int dataStartRow3 = 4; // 5행부터 데이터 시작
    int lastRowIndex3 = sheet.getLastRowNum();
    int rowsToAdd3 = workList.size();

    // 제목/기간 셋팅
    setCellValueSafe(sheet, 0, 0, "▣ S/W 유지보수 서비스 상세내역");
    setCellValueSafe(sheet, 1, 0, String.format("유지보수 기간: %s ~ %s", params.getStartDate(), params.getEndDate()));

    String currentSubCd = "";
  String swName = "";
  String supportCompanyNm = "";
  
  if (workList != null && !workList.isEmpty()) {
    // workList에 데이터가 있으면 workList에서 가져오기
    Map<String, Object> firstRow = workList.get(0);
    currentSubCd = (String) firstRow.get("sub_cd");
    
    setCellValueSafe(sheet, 1, 9, currentSubCd);
    setCellValueSafe(sheet, 1, 11, firstRow.get("sw_nm"));

    supportCompanyNm = findCustomerCdBySubCd(reportData, currentSubCd);
    setCellValueSafe(sheet, 1, 12, supportCompanyNm);
  } else if (reportData != null && !reportData.isEmpty()) {
    //workList가 비어있으면 reportData에서 첫 번째 항목 정보 사용
    String sheetName = workbook.getSheetName(workbook.getSheetIndex(sheet));
    
    for (Map<String, Object> report : reportData) {
      String reportSubCd = (String) report.get("sub_cd");
      if (sheetName.equals(reportSubCd)) {
        currentSubCd = reportSubCd;
        swName = report.get("sw_nm") != null ? report.get("sw_nm").toString() : "";
        supportCompanyNm = report.get("support_company_nm") != null ? report.get("support_company_nm").toString() : "";
        
        setCellValueSafe(sheet, 1, 9, currentSubCd);
        setCellValueSafe(sheet, 1, 11, swName);
        setCellValueSafe(sheet, 1, 12, supportCompanyNm);
        setCellValueSafe(sheet, 4, 0, swName);
        setCellValueSafe(sheet, 4, 1, currentSubCd);
        setCellValueSafe(sheet, 4, 8, supportCompanyNm);
        setCellValueSafe(sheet, 4, 11, "지원내역 없음");
        break;
      }
    }
  }
    XSSFRow templateRow3 = sheet.getRow(dataStartRow3);

    // 행 이동
    if (rowsToAdd3 > 1) {
      sheet.shiftRows(dataStartRow3, lastRowIndex3, rowsToAdd3 - 1, true, true);
    }

    // 데이터 입력
    for (int i = 0; i < workList.size(); i++) {
      Map<String, Object> rowMap = workList.get(i);

      XSSFRow newRow = addRowWithStyleAndFormula(sheet, workbook, dataStartRow3 + i, templateRow3);

      setCellValueSafe(newRow, 0, rowMap.get("sw_nm"));
      setCellValueSafe(newRow, 1, rowMap.get("sub_cd"));
      setCellValueSafe(newRow, 2, rowMap.get("service_nm"));
      setDateValueSafe(newRow, 3, rowMap.get("req_dt"));
      setCellValueSafe(newRow, 4, rowMap.get("req_user_dept_nm1"));
      setCellValueSafe(newRow, 5, rowMap.get("req_user_nm"));
      setCellValueSafe(newRow, 6, rowMap.get("req_contents"));
      setDateValueSafe(newRow, 7, rowMap.get("proc_dt"));
      setCellValueSafe(newRow, 8, rowMap.get("proc_company_nm"));
      setCellValueSafe(newRow, 9, rowMap.get("proc_user_nm"));
      setCellValueSafe(newRow, 10, rowMap.get("proc_support_nm"));
      setCellValueSafe(newRow, 11, rowMap.get("proc_contents"));
      setCellValueSafe(newRow, 12, rowMap.get("status_nm"));
      setCellValueSafe(newRow, 13, rowMap.get("remark"));
    }
  }
  //첫번째시트 subCode에 해당하는 회사이름 가져오기
  private String findCustomerCdBySubCd(List<Map<String, Object>> reportData, String subCd) {
    if (subCd == null) return "";
    
    for (Map<String, Object> row : reportData) {
      String rowSubCd = (String) row.get("sub_cd");
      if (subCd.equals(rowSubCd)) {
        Object customerCd = row.get("support_company_nm"); // 또는 "customerCd" - 실제 컬럼명 확인
        return customerCd != null ? customerCd.toString() : "";
      }
    }
    
    return ""; // 못 찾으면 빈 문자열
  }

  private static void setDateValueSafe(Row row, int colIdx, Object value) {
    Cell cell = row.getCell(colIdx);
    if (cell == null) cell = row.createCell(colIdx);
    
    if (value == null) {
        cell.setBlank();
        return;
    }
    
    try {
        if (value instanceof java.util.Date) {
            // Date 객체면 그대로 사용
            cell.setCellValue((java.util.Date) value);
        } else if (value instanceof java.sql.Date) {
            // SQL Date면 util.Date로 변환
            cell.setCellValue(new java.util.Date(((java.sql.Date) value).getTime()));
        } else if (value instanceof java.sql.Timestamp) {
            // Timestamp면 util.Date로 변환
            cell.setCellValue(new java.util.Date(((java.sql.Timestamp) value).getTime()));
        } else if (value instanceof String) {
            // 문자열이면 파싱 시도
            String dateStr = value.toString();
            
            // yyyy-MM-dd 형식 파싱
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(dateStr.substring(0, 10));
                cell.setCellValue(date);
            } else {
                // 파싱 실패하면 문자열로 입력
                cell.setCellValue(dateStr);
            }
        } else {
            // 알 수 없는 타입이면 문자열로 변환
            cell.setCellValue(value.toString());
        } 
    } catch (Exception e) {
        // 오류 발생 시 문자열로 입력
        cell.setCellValue(value.toString());
    }
  }

  private XSSFRow addRowWithStyleAndFormula(XSSFSheet sheet, XSSFWorkbook workbook, int newRowIndex, XSSFRow templateRow) {
    XSSFRow newRow = sheet.getRow(newRowIndex);
    
    if (newRow == null) {
      newRow = sheet.createRow(newRowIndex);
    }

    if (templateRow != null) {
      // 행 높이 복사
      newRow.setHeight(templateRow.getHeight());

      // 각 셀의 스타일 복사
      for (int i = 0; i <= templateRow.getLastCellNum(); i++) {
        XSSFCell refCell = templateRow.getCell(i);
        
        if (refCell != null) {
          XSSFCell newCell = newRow.getCell(i);
          if (newCell == null) {
            newCell = newRow.createCell(i);
          }

          // 스타일 복사
          if (refCell.getCellStyle() != null) {
            newCell.setCellStyle(refCell.getCellStyle());
          }

          // 수식이 있으면 복사 (셀 병합 등 특수한 경우 처리)
          if (refCell.getCellType() == CellType.FORMULA) {
            try {
              newCell.setCellFormula(refCell.getCellFormula());
            } catch (Exception e) {
              // 수식 복사 실패 시 무시
            }
          }
        }
      }
    }
    return newRow;
  }



  private static void setCellValueSafe(Row row, int colIdx, Object value) {
    Cell cell = row.getCell(colIdx);
    if (cell == null) cell = row.createCell(colIdx);
    if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue());
    } else if (value != null) {
        cell.setCellValue(value.toString());
    } else {
        cell.setBlank();
    }
  }

  private static void setCellValueSafe(XSSFSheet sheet, int rowIdx, int colIdx, Object value) {
    XSSFRow row = sheet.getRow(rowIdx);
    if (row == null) row = sheet.createRow(rowIdx);

    XSSFCell cell = row.getCell(colIdx);
    if (cell == null) cell = row.createCell(colIdx);

    if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue());
    } else if (value != null) {
        cell.setCellValue(value.toString());
    } else {
        cell.setBlank();
    }
  }
    
  @Override
  public XSSFWorkbook createWorkResultExcelFile2(List<?> results, List<ExcelField> fields) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = WorkSheetLayout.createSheetLayout2(workbook, "Sub Code N분기", fields);

    Font bodyFont = WorkCellStyle.setCellFont(workbook.createFont(), 230, false);
    CellStyle bodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.CENTER, true, false, CELL.BODY);
    CellStyle alignBodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.LEFT, true, false, CELL.BODY_ALIGN);

    int startRowIndex = 4;

    for (var gridRowData : results) {
        XSSFRow row = sheet.createRow(startRowIndex++);
        List<String> rowData = getFieldValues(gridRowData);

        for (int i = 0; i < fields.size(); i++) {
            ExcelField field = fields.get(i);
            String val = rowData.get(i);

            XSSFCell cell = row.createCell(i + 1);
            cell.setCellValue(val == null || val.equals("null") ? "" : val);
            cell.setCellStyle(field.getAlign() == ALIGN.CENTER ? bodyCellStyle : alignBodyCellStyle);
        }
    }

    if (!results.isEmpty()) {
        final int filterFirstRow = 4;
        final int filterLastRow = results.size() - 1 + filterFirstRow;
        sheet.setAutoFilter(CellRangeAddress.valueOf(String.format("B%d:W%d", filterFirstRow, filterLastRow)));
    }

    return workbook;
}

  @Override
  public List<String> getFieldValues(Object data) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getFieldValues'");
  }

  @Override
  public SXSSFWorkbook createWorkResultExcelFile(List<?> results, List<ExcelField> fields) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createWorkResultExcelFile'");
  }
}
