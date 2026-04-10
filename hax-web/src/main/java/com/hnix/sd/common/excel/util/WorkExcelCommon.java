package com.hnix.sd.common.excel.util;

import com.hnix.sd.common.excel.dto.ExcelField;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface WorkExcelCommon {

    /**
     * Excel 파일 객체를 생성해서 return 하는 함수
     * @param results
     * @param fields
     * @return
     */
    SXSSFWorkbook createWorkResultExcelFile(List<?> results, List<ExcelField> fields);

    /**
     * Excel 파일 객체를 생성해서 return 하는 함수
     * @param results
     * @param fields
     * @return
     */
    XSSFWorkbook createWorkResultExcelFile2(List<?> results, List<ExcelField> fields);

    /**
     * DTO 객체에 있는 모든 필드를 순차적으로 가져오기 위한 함수.
     * DTO 데이터의 값을 Formatting, 예외처리하는 로직도 작성.
     * @param data
     * @return
     */
    List<String> getFieldValues(Object data);

}
