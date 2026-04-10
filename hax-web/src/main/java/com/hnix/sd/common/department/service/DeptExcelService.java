package com.hnix.sd.common.department.service;

import com.hnix.sd.common.department.dto.manage.DeptExcelDto;
import com.hnix.sd.common.department.dto.manage.DeptRegisterDto;
import com.hnix.sd.common.department.dto.manage.FailedDeptDto;
import com.hnix.sd.common.department.dto.tree.DeptTreeDto;
import com.hnix.sd.common.code.service.CodeService;
import com.hnix.sd.common.excel.dto.ExcelField;
import com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import com.hnix.sd.common.excel.util.ExcelFormat.CELL;
import com.hnix.sd.common.excel.util.WorkCellStyle;
import com.hnix.sd.common.excel.util.WorkSheetLayout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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


@Slf4j
@RequiredArgsConstructor
@Service
public class DeptExcelService {
    private final DeptTreeService deptTreeService;
    private final DeptService deptService;
    private final CodeService codeService;
    private final com.hnix.sd.common.department.dao.DepartmentDao departmentDao;

	public SXSSFWorkbook exportDeptExcel(List<String> deptCd) {
        List<DeptTreeDto> treeList = (deptCd == null || deptCd.isEmpty()) 
        ? deptTreeService.getTreeDataFromprntDeptNm()
        : deptTreeService.getTreeDataFromprntDeptNmByDeptCd(deptCd);

        List<DeptExcelDto> excelDataList = new ArrayList<>();

        for (DeptTreeDto tree : treeList) {
            DeptExcelDto dto = new DeptExcelDto(); // 기본 생성자 사용

            dto.setPrntDeptNm(tree.getPrntDeptNm());
            dto.setPrntDeptCd(tree.getPrntDeptCd());
            dto.setDeptCd(tree.getDeptCd());
            dto.setDeptNm(tree.getDeptNm());
            dto.setDeptTypeCd(tree.getDeptTypeCd() == null ? null : tree.getDeptTypeCd().toString());
            dto.setCompClassCd(tree.getCompClassCd());
            dto.setCompTypeCd(tree.getCompanyTypeCd() == null ? null : tree.getCompanyTypeCd().toString());
            dto.setUseYn(tree.getUseYn() == null ? null : tree.getUseYn().toString());
            dto.setDeptDesc(tree.getDeptDesc());

            excelDataList.add(dto);
        }

        List<ExcelField> fields = new ArrayList<>();
        fields.add(new ExcelField("prntDeptNm", "상위부서", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("prntDeptCd", "상위부서CODE", ALIGN.LEFT, 6000));
        fields.add(new ExcelField("deptNm", "부서", ALIGN.LEFT, 8000));
        fields.add(new ExcelField("deptCd", "부서CODE", ALIGN.LEFT, 6000));
        fields.add(new ExcelField("deptTypeNm", "부서구분", ALIGN.LEFT, 3000));
        fields.add(new ExcelField("deptTypeCd", "부서구분CODE", ALIGN.CENTER, 3000));
        fields.add(new ExcelField("companyTypeNm", "회사구분", ALIGN.LEFT, 3000));
        fields.add(new ExcelField("companyTypeCd", "회사구분CODE", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("useYn", "사용여부", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("deptDesc", "부서설명", ALIGN.LEFT, 6000));

        return createDeptExcelFile(excelDataList, fields);
    }

    private SXSSFWorkbook createDeptExcelFile(List<DeptExcelDto> results, List<ExcelField> fields) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = WorkSheetLayout.createSheetLayout(workbook, "회사부서 관리", fields);

        Font bodyFont = WorkCellStyle.setCellFont(workbook.createFont(), 230, false);
        CellStyle bodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.CENTER, true, false, CELL.BODY);
        CellStyle alignBodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.LEFT, true, false, CELL.BODY_ALIGN);

        int startRowIndex = 4;

        for (DeptExcelDto dto : results) {
            SXSSFRow row = sheet.createRow(startRowIndex++);
            List<String> rowData = getDeptExcelFieldValues(dto);

            for (int i = 0; i < fields.size(); i++) {
                ExcelField field = fields.get(i);
                String val = rowData.get(i);
                SXSSFCell cell = row.createCell(i + 1);
                cell.setCellValue(val == null ? "" : val);
                cell.setCellStyle(field.getAlign() == ALIGN.CENTER ? bodyCellStyle : alignBodyCellStyle);
            }
        }

        if (!results.isEmpty()) {
            int filterFirstRow = 4;
            int filterLastRow = results.size() - 1 + filterFirstRow;
            sheet.setAutoFilter(CellRangeAddress.valueOf(String.format("B%d:P%d", filterFirstRow, filterLastRow)));
        }

        return workbook;
    }

    private List<String> getDeptExcelFieldValues(DeptExcelDto dto) {
        return List.of(
			dto.getPrntDeptNm() == null ? "" : dto.getPrntDeptNm(),
            dto.getPrntDeptCd() == null ? "" : dto.getPrntDeptCd(),
            dto.getDeptNm() == null ? "" : dto.getDeptNm(),
            dto.getDeptCd() == null ? "" : dto.getDeptCd(),
            codeService.getSubCodeNameByCodeVal("DEPT_TYPE", dto.getDeptTypeCd()),
			dto.getDeptTypeCd() == null ? "" : dto.getDeptTypeCd(),
            codeService.getSubCodeNameByCodeVal("COMP_TYPE", dto.getCompTypeCd()),
			dto.getCompTypeCd() == null ? "" : dto.getCompTypeCd(),
			dto.getUseYn() == null ? "" : dto.getUseYn(),
            dto.getDeptDesc() == null ? "" : dto.getDeptDesc()
        );
    }

    // 엑셀 업로드
    public List<DeptExcelDto> parseDeptExcel(MultipartFile file) {
        List<DeptExcelDto> dtoList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowIndex = 0;

            // 1단계: 먼저 엑셀 데이터만 읽기
            while (rows.hasNext()) {
                Row row = rows.next();

                if (rowIndex++ < 2) continue; // 헤더 스킵

                DeptExcelDto dto = new DeptExcelDto();

                // 각 셀 값 가져오기
                dto.setPrntDeptNm(getCellValue(row, 1));
                dto.setPrntDeptCd(getCellValue(row, 2));
                dto.setDeptNm(getCellValue(row, 3));
                dto.setDeptCd(getCellValue(row, 4).trim());
                dto.setDeptTypeNm(getCellValue(row, 5));
                dto.setDeptTypeCd(getCellValue(row, 6));
                dto.setCompTypeNm(getCellValue(row, 7));
                dto.setCompTypeCd(getCellValue(row, 8));
                dto.setUseYn(getCellValue(row, 9));
                dto.setDeptDesc(getCellValue(row, 10));

                // DTO 자체 검증
                dto.setErrorMsg(dto.validate(departmentDao::existsByDeptTypeCd,
                departmentDao::existsByCompanyTypeCd));

                // 단건으로 존재 여부 확인
                if (departmentDao.existsByDeptCd(dto.getDeptCd())) {
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

    // 엑셀 업로드 후 DB 저장
    public List<FailedDeptDto> storeDeptList(List<DeptExcelDto> excelDtoList) {
    // DTO -> RegisterDto 변환
    List<DeptRegisterDto> registerList = excelDtoList.stream()
        .map(excel -> {
            DeptRegisterDto dto = new DeptRegisterDto();
            dto.setDeptCd(excel.getDeptCd());
            dto.setDeptNm(excel.getDeptNm());
            dto.setPrntDeptCd(excel.getPrntDeptCd());
            dto.setDeptTypeCd(excel.getDeptTypeCd().charAt(0));
            dto.setCompanyTypeCd(excel.getCompTypeCd().charAt(0));
            dto.setUseYn(excel.getUseYn().charAt(0));
            dto.setDeptDesc(excel.getDeptDesc());
            return dto;
        })
        .collect(Collectors.toList());

        return deptService.updateMultipleDepartment(registerList);
    }   
}
