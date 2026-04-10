package com.hnix.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;


public class test {
  public static void main(String[] args) {
    System.out.println("✅ 프로그램이 정상 실행되었습니다.");
    String templatePath = "서비스활동보고서 양식.xlsx";
    String outputPath = "서비스활동 보고서 양식-테스트.xlsx";

    try (FileInputStream fis = new FileInputStream(templatePath);
      Workbook workbook = new XSSFWorkbook(fis)) {

        // --- 1번째 시트 ---
        Sheet sheet1 = workbook.getSheetAt(0);
        setCellValueSafe(sheet1, 0, 0, "▣ 2025년 S/W별 기술지원 현황_Subcode순 집계(회사)");
        setCellValueSafe(sheet1, 1, 0, "유지보수 기간: 2025년 09월 11일 ~ 2025년 09월 30일");
        // --- 하드코딩 값 준비 ---
        // 각 행마다 넣을 컬럼과 값 저장
        List<Map<Integer, Object>> rowValues = new ArrayList<>();

        Map<Integer, Object> row1 = new HashMap<>();
        row1.put(4, 2);      
        row1.put(5, 3);      
        rowValues.add(row1);

        Map<Integer, Object> row2 = new HashMap<>();
        row2.put(4, 5); 
        row2.put(5, 4);      
        rowValues.add(row2);

        int rowsToAdd = rowValues.size(); // 추가할 행 수
        int lastRowIndex = sheet1.getLastRowNum(); 
        int referenceRowIndex = lastRowIndex - 1; // 합계 바로 위 데이터 행

        // 1. 합계 행을 아래로 이동
        sheet1.shiftRows(referenceRowIndex + 1, lastRowIndex, rowsToAdd, true, true);

        // 2. 새 행 추가 + 값 입력
        for (int i = 0; i < rowsToAdd; i++) {
            Row newRow = addRowWithStyleAndFormula(sheet1, workbook, referenceRowIndex + i);

            // 하드코딩 값 입력
            Map<Integer, Object> values = rowValues.get(i);
            for (Map.Entry<Integer, Object> entry : values.entrySet()) {
                int col = entry.getKey();
                Object val = entry.getValue();

                Cell cell = newRow.getCell(col);
                if (cell == null) cell = newRow.createCell(col);

                if (val instanceof Number) {
                    cell.setCellValue(((Number) val).doubleValue());
                } else if (val != null) {
                    cell.setCellValue(val.toString());
                } else {
                    cell.setBlank();
                }
            }
        }

        // 3. 합계 행 수식 갱신
        Row sumRow = sheet1.getRow(referenceRowIndex + 1 + rowsToAdd); // shiftRows 후 새 합계 행
        int[] sumCols = {3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
        for (int col : sumCols) {
            Cell sumCell = sumRow.getCell(col);
            if (sumCell != null && sumCell.getCellType() == CellType.FORMULA) {
                String oldFormula = sumCell.getCellFormula();
                String newFormula = shiftSumFormula(oldFormula, rowsToAdd);
                sumCell.setCellFormula(newFormula);
            }
        }

        Row firstSumRow6 = sheet1.getRow(5); // 6번째 행 -> index 5
        for (int col : sumCols) {
            Cell sumCell = firstSumRow6.getCell(col);
            if (sumCell != null && sumCell.getCellType() == CellType.FORMULA) {
                String oldFormula = sumCell.getCellFormula();
                String newFormula = shiftSumFormula(oldFormula, rowsToAdd); // 필요 시 shift 안 해도 됨
                sumCell.setCellFormula(newFormula);
            }
        }


        // --- 2번째 시트 ---
        Sheet sheet2 = workbook.getSheetAt(1);
        setCellValueSafe(sheet2, 0, 0, "▣ 2025년 S/W별 기술지원 현황_Subcode순 집계(회사)");
        setCellValueSafe(sheet2, 1, 0, "유지보수 기간: 2025년 09월 11일 ~ 2025년 09월 30일");
        // --- 하드코딩 값 준비 ---
        // 각 행마다 넣을 컬럼과 값 저장
        List<Map<Integer, Object>> rowValues2 = new ArrayList<>();

        Map<Integer, Object> row3 = new HashMap<>();
        row3.put(0, "가");   // A열
        row3.put(4, 2);      // 
        rowValues2.add(row3);

        Map<Integer, Object> row4 = new HashMap<>();
        row4.put(0, "나");   // A열
        row4.put(4, 5);      
        rowValues2.add(row4);

        int rowsToAdd2 = rowValues2.size(); // 추가할 행 수
        int lastRowIndex2 = sheet2.getLastRowNum(); 
        int referenceRowIndex2 = lastRowIndex2 - 1; // 합계 바로 위 데이터 행

        // 1. 합계 행을 아래로 이동
        sheet2.shiftRows(referenceRowIndex2 + 1, lastRowIndex2, rowsToAdd2, true, true);

        // 2. 새 행 추가 + 값 입력
        for (int i = 0; i < rowsToAdd2; i++) {
            Row newRow = addRowWithStyleAndFormula(sheet2, workbook, referenceRowIndex2 + i);

            // 하드코딩 값 입력
            Map<Integer, Object> values = rowValues2.get(i);
            for (Map.Entry<Integer, Object> entry : values.entrySet()) {
                int col = entry.getKey();
                Object val = entry.getValue();

                Cell cell = newRow.getCell(col);
                if (cell == null) cell = newRow.createCell(col);

                if (val instanceof Number) {
                    cell.setCellValue(((Number) val).doubleValue());
                } else if (val != null) {
                    cell.setCellValue(val.toString());
                } else {
                    cell.setBlank();
                }
            }
        }

        // 3. 합계 행 수식 갱신
        Row sumRow2 = sheet2.getRow(referenceRowIndex2 + 1 + rowsToAdd2); // shiftRows 후 새 합계 행
        int[] sumCols2 = {3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
        for (int col : sumCols2) {
            Cell sumCell = sumRow2.getCell(col);
            if (sumCell != null && sumCell.getCellType() == CellType.FORMULA) {
                String oldFormula = sumCell.getCellFormula();
                String newFormula = shiftSumFormula(oldFormula, rowsToAdd2);
                sumCell.setCellFormula(newFormula);
            }
        }

        Row twoSumRow6 = sheet2.getRow(5); // 6번째 행 -> index 5
        for (int col : sumCols2) {
            Cell sumCell = twoSumRow6.getCell(col);
            if (sumCell != null && sumCell.getCellType() == CellType.FORMULA) {
                String oldFormula = sumCell.getCellFormula();
                String newFormula = shiftSumFormula(oldFormula, rowsToAdd2); // 필요 시 shift 안 해도 됨
                sumCell.setCellFormula(newFormula);
            }
        }

        // --- 3번째 시트 (원본 양식) ---
        Sheet sheet3 = workbook.getSheetAt(2);
        setCellValueSafe(sheet3, 1, 0, "유지보수 기간: 2025년 09월 11일 ~ 2025년 09월 30일");
        setCellValueSafe(sheet3, 1, 9, "코드");
        setCellValueSafe(sheet3, 1, 11, "소프트웨어명");
        setCellValueSafe(sheet3, 1, 12,"협력사명");
        setCellValueSafe(sheet3, 4, 0, "다");
        setCellValueSafe(sheet3, 4, 1, "A");

        Sheet copied = workbook.cloneSheet(2); // 2 = 3번째 시트 인덱스
        int newIndex = workbook.getSheetIndex(copied);
        
        // 새 시트 이름 변경
        workbook.setSheetName(newIndex, "테스트");
    
        // 필요한 값 수정
        setCellValueSafe(copied, 1, 0, "유지보수 기간: 2025년 09월 11일 ~ 2025년 09월 30일");
        setCellValueSafe(copied, 1, 9, "코드2");
        setCellValueSafe(copied, 1, 11, "소프트웨어명2");
        setCellValueSafe(copied, 1, 12,"협력사명2");
        setCellValueSafe(copied, 4, 0, "라");

        int realLastRow = -1;
        for (int i = 0; i <= sheet3.getLastRowNum(); i++) {
            Row row = sheet3.getRow(i);
            if (row == null) continue;

            boolean hasData = false;
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    hasData = true;
                    break;
                }
            }

            if (hasData) realLastRow = i;
        }

        System.out.println("실제 데이터 마지막 행 = " + realLastRow);

        // ------------------------------------
        // 2. 새 행 생성
        Row referenceRow3 = sheet3.getRow(realLastRow);
        Row newRow3 = sheet3.createRow(realLastRow + 1);

        // 3. 높이 복사
        if (referenceRow3 != null) {
            newRow3.setHeight(referenceRow3.getHeight());
        }

        // ------------------------------------
        // 4. 셀 스타일 및 값 복사
        if (referenceRow3 != null) {
            for (int i = 0; i < referenceRow3.getLastCellNum(); i++) {
                Cell refCell = referenceRow3.getCell(i);
                Cell newCell = newRow3.createCell(i);

                if (refCell != null) {
                    // 스타일 복사
                    CellStyle newStyle = workbook.createCellStyle();
                    newStyle.cloneStyleFrom(refCell.getCellStyle());
                    newCell.setCellStyle(newStyle);

                    // 새 값 입력 (원하는 대로 수정)
                    if (i == 0) newCell.setCellValue("추가된 행");
                }
            }
        }
        
       workbook.setForceFormulaRecalculation(true);

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
          workbook.write(fos);
          System.out.println("엑셀 저장 완료"+outputPath);
        }

      } catch (Exception e) {
      e.printStackTrace();  // ✅ 오류 내용 콘솔에 출력
    }
	}

  private static void setCellValueSafe(Sheet sheet, int rowIdx, int colIdx, Object value) {
    Row row = sheet.getRow(rowIdx);
    if (row == null) row = sheet.createRow(rowIdx);

    Cell cell = row.getCell(colIdx);
    if (cell == null) cell = row.createCell(colIdx);

    if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue()); // 숫자
    } else if (value != null) {
        cell.setCellValue(value.toString()); // 문자열
    } else {
        cell.setBlank(); // null 방지
    }
  }

      /**
     * 마지막 데이터 행 아래에 새 Row 추가 + 스타일 복사 + 수식 자동 업데이트
     */
    public static Row addRowWithStyleAndFormula(Sheet sheet, Workbook workbook, int lastDataRowIndex) {

      Row referenceRow = sheet.getRow(lastDataRowIndex);
      if (referenceRow == null) return sheet.createRow(lastDataRowIndex + 1);

      // 1. 새 Row 생성
      Row newRow = sheet.createRow(lastDataRowIndex + 1);
      newRow.setHeight(referenceRow.getHeight());

      // 2. 셀 복사
      for (int i = 0; i < referenceRow.getLastCellNum(); i++) {
          Cell refCell = referenceRow.getCell(i);
          Cell newCell = newRow.createCell(i);

          if (refCell != null) {
              // 스타일 복사
              CellStyle newStyle = workbook.createCellStyle();
              newStyle.cloneStyleFrom(refCell.getCellStyle());
              newCell.setCellStyle(newStyle);

              // 값 또는 수식 복사
              switch (refCell.getCellType()) {
                  case NUMERIC:
                      newCell.setCellValue(refCell.getNumericCellValue());
                      break;
                  case STRING:
                      newCell.setCellValue(refCell.getStringCellValue());
                      break;
                  case FORMULA:
                      // 수식 자동 업데이트 (행 번호만 +1)
                      String formula = refCell.getCellFormula();
                      formula = shiftFormulaRow(formula, 1); 
                      newCell.setCellFormula(formula);
                      break;
                  case BOOLEAN:
                      newCell.setCellValue(refCell.getBooleanCellValue());
                      break;
                  case BLANK:
                      newCell.setBlank();
                      break;
                  default:
                      break;
              }
          }
      }

      return newRow;
    }

    /**
    * 수식 안의 행 번호를 shift 만큼 증가시켜서 자동 업데이트
    * 단순 SUM/AVERAGE 등의 상대 참조만 처리
    */
    public static String shiftFormulaRow(String formula, int rowShift) {
      Pattern p = Pattern.compile("([A-Z]+)(\\d+)");
      Matcher m = p.matcher(formula);
      StringBuffer sb = new StringBuffer();
  
      while (m.find()) {
          String col = m.group(1);
          int row = Integer.parseInt(m.group(2)) + rowShift;
          m.appendReplacement(sb, col + row);
      }
      m.appendTail(sb);
  
      return sb.toString();
    }

    public static String shiftSumFormula(String formula, int rowShift) {
      Pattern p = Pattern.compile(":(\\D+)(\\d+)"); // :D8 같은 부분 찾기
      Matcher m = p.matcher(formula);
      StringBuffer sb = new StringBuffer();
      
      while (m.find()) {
          String col = m.group(1);
          int row = Integer.parseInt(m.group(2)) + rowShift;
          m.appendReplacement(sb, ":" + col + row);
      }
      m.appendTail(sb);
      
      return sb.toString();
    }
}
