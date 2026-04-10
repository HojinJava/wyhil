package com.hnix.sd.work.software.contract.service;

import com.hnix.sd.common.department.dao.DepartmentDao;
import com.hnix.sd.common.department.dto.DepartmentDto;
import com.hnix.sd.work.software.contract.dao.ContractDao;
import com.hnix.sd.work.software.contract.dto.*;
import com.hnix.sd.work.software.partner.dao.PartnerDao;
import com.hnix.sd.work.software.partner.dto.PartnerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
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

import com.hnix.sd.common.code.dto.SubCodeDto;
import com.hnix.sd.core.dto.PageResponseDto;
import com.hnix.sd.common.code.service.CodeService;
import com.hnix.sd.common.excel.dto.ExcelField;
import com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import com.hnix.sd.common.excel.util.ExcelFormat.CELL;
import com.hnix.sd.common.excel.util.WorkCellStyle;
import com.hnix.sd.common.excel.util.WorkSheetLayout;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContractExcelService {
    private final ContractGridService contractGridService;
    private final ContractService contractService;
    private final CodeService codeService;
    private final ContractDao contractDao;
    private final PartnerDao partnerDao;
    private final DepartmentDao departmentDao;

    public SXSSFWorkbook exportContractExcel(ContractSearchDto searchDto) {
        List<SubCodeDto> partnerContractList = codeService.getSubCodeFromGroupCodeCd("COMP_CONTRACT");
        Map<String, String> partnerContractMap = partnerContractList.stream()
            .collect(Collectors.toMap(SubCodeDto::getCodeCd, SubCodeDto::getCodeText));

        // pageSize를 큰 값으로 설정해서 전체 데이터 가져오기
        searchDto.setPageSize(Integer.MAX_VALUE);
        searchDto.setPageNo(0);
        
        PageResponseDto<ContractGridInfoDto> contractPage = contractGridService.getContractPagination(searchDto);
        List<ContractGridInfoDto> contractList = contractPage.getContent();
        
        List<ContractExcelDto> excelDataList = new ArrayList<>();

        for (ContractGridInfoDto contractGridDto : contractList) {
            String codeText = partnerContractMap.get(contractGridDto.getPartnerContractCd());

            ContractExcelDto dto = new ContractExcelDto(
                contractGridDto.getContractYear(),
                contractGridDto.getSubCd(),
                contractGridDto.getSwName(),
                contractGridDto.getCustomerCompanyNm(),
                contractGridDto.getCustomerDepartmentNm(),
                contractGridDto.getCustomerManageNm(),
                contractGridDto.getDeptCd(),
                contractGridDto.getPartnerCompanyNm(),
                codeText != null ? codeText : contractGridDto.getPartnerContractCd(),
                contractGridDto.getContractStDt(),
                contractGridDto.getContractEdDt(),
                contractGridDto.getContractNo(),
                contractGridDto.getRemark()
            );
            excelDataList.add(dto);
        }

        List<ExcelField> fields = new ArrayList<>();
        fields.add(new ExcelField("contractYear", "계약년도", ALIGN.LEFT, 3000));
        fields.add(new ExcelField("subCd", "SUB CODE", ALIGN.LEFT, 3000));
        fields.add(new ExcelField("swName", "소프트웨어", ALIGN.LEFT, 7000));
        fields.add(new ExcelField("customerCompanyNm", "고객사", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("customerDepartmentNm", "사업부", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("customerManageNm", "관리부서", ALIGN.LEFT, 7000));
        fields.add(new ExcelField("deptCd", "관리부서CODE", ALIGN.LEFT, 8000));
        fields.add(new ExcelField("partnerCompanyNm", "협력사", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("partnerContractCd", "계약관계", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("contractStDt", "시작일", ALIGN.LEFT, 6000));
        fields.add(new ExcelField("contractEdDt", "종료일", ALIGN.LEFT, 6000));
        fields.add(new ExcelField("contractNo", "계약번호", ALIGN.LEFT, 12000));
        fields.add(new ExcelField("remark", "비고", ALIGN.LEFT, 7000));

        return createContractExcelFile(excelDataList, fields);
    }

    private SXSSFWorkbook createContractExcelFile(List<ContractExcelDto> results, List<ExcelField> fields) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = WorkSheetLayout.createSheetLayout(workbook, "소프트웨어 협력사 계약 관리", fields);

        Font bodyFont = WorkCellStyle.setCellFont(workbook.createFont(), 230, false);
        CellStyle bodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.CENTER, true, false, CELL.BODY);
        CellStyle alignBodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.LEFT, true, false, CELL.BODY_ALIGN);

        int startRowIndex = 4;

        for (ContractExcelDto dto : results) {
            SXSSFRow row = sheet.createRow(startRowIndex++);
            List<Object> rowData = getContractExcelFieldValues(dto);

            for (int i = 0; i < fields.size(); i++) {
                ExcelField field = fields.get(i);
                String val = (String) rowData.get(i);
                SXSSFCell cell = row.createCell(i + 1);
                cell.setCellValue(val == null ? "" : val);
                cell.setCellStyle(field.getAlign() == ALIGN.CENTER ? bodyCellStyle : alignBodyCellStyle);
            }
        }

        return workbook;
    }

    private List<Object> getContractExcelFieldValues(ContractExcelDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return List.of(
          dto.getContractYear() == null ? "" : dto.getContractYear(),
          dto.getSubCd() == null ? "" : dto.getSubCd(),
          dto.getSwName() == null ? "" : dto.getSwName(),
          dto.getCustomerCompanyNm() == null ? "" : dto.getCustomerCompanyNm(),
          dto.getCustomerDepartmentNm() == null ? "" : dto.getCustomerDepartmentNm(),
          dto.getCustomerManageNm() == null ? "" : dto.getCustomerManageNm(),
          dto.getDeptCd() == null ? "" : dto.getDeptCd(),
          dto.getPartnerCompanyNm() == null ? "" : dto.getPartnerCompanyNm(),
          dto.getPartnerContractCd() == null ? "" : dto.getPartnerContractCd(),
          dto.getContractStDt() == null ? "" : dto.getContractStDt().format(formatter),
          dto.getContractEdDt() == null ? "" : dto.getContractEdDt().format(formatter), 
          dto.getContractNo() == null ? "" : dto.getContractNo(),
          dto.getRemark() == null ? "" : dto.getRemark()
        );
    }

    public List<ContractExcelDto> parseContractExcel(MultipartFile file) {
        List<ContractExcelDto> dtoList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowIndex = 0;

            while (rows.hasNext()) {
                Row row = rows.next();

                if (rowIndex++ < 2) continue; // 헤더 스킵

                ContractExcelDto dto = new ContractExcelDto();

                // 각 셀 값 가져오기
                dto.setContractYear(getCellValue(row, 1));
                dto.setSubCd(getCellValue(row, 2));
                dto.setSwName(getCellValue(row, 3));
                dto.setCustomerCompanyNm(getCellValue(row, 4));
                dto.setCustomerDepartmentNm(getCellValue(row, 5));
                dto.setCustomerManageNm(getCellValue(row, 6));
                dto.setDeptCd(getCellValue(row, 7));
                dto.setPartnerCompanyNm(getCellValue(row, 8));
                dto.setPartnerContractCd(getCellValue(row, 9));
                String startDateStr = getCellValue(row, 10);
                String endDateStr = getCellValue(row, 11);

                dto.setContractStDtStr(startDateStr); 
                dto.setContractEdDtStr(endDateStr);

                dto.setContractNo(getCellValue(row, 12));
                dto.setRemark(getCellValue(row, 13));

                dto.setContractStDt(parseDateTime(startDateStr));
                dto.setContractEdDt(parseDateTime(endDateStr));

                PartnerDto partner = partnerDao.findBySubCode(dto.getSubCd());
                DepartmentDto department = departmentDao.findByDeptCd(dto.getDeptCd());

                // DTO 자체 검증
                dto.setErrorMsg(dto.validate(partner, department));

                Optional<ContractDto> contractOpt = contractDao.findById(dto.getContractNo());

                // DB 존재 여부 체크
                if (contractOpt.isPresent()) {
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

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) return null;
    
        // 가능한 모든 패턴
        String[] patterns = {
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        };
    
        for (String pattern : patterns) {
            try {
                DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
                if (pattern.equals("yyyy-MM-dd")) {
                    // 날짜만 있는 경우 → 00:00:00 으로 변환
                    return LocalDateTime.parse(value + " 00:00:00",
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                return LocalDateTime.parse(value, f);
            } catch (Exception ignore) {}
        }
    
        throw new RuntimeException("날짜 형식을 파싱할 수 없습니다: " + value);
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
    
        if (cell == null) {
            return "";
        }
    
        switch (cell.getCellType()) {
    
            case STRING:
                return cell.getStringCellValue().trim();
    
            case NUMERIC:
                // 날짜인지 확인
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime dt = cell.getLocalDateTimeCellValue();
                    return dt.toString(); // "2025-10-28T00:00"
                }
                // 숫자면 그냥 숫자를 문자열로
                return String.valueOf(cell.getNumericCellValue());
    
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
    
            case FORMULA:
                try {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        LocalDateTime dt = cell.getLocalDateTimeCellValue();
                        return dt.toString();
                    }
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
    
            default:
                return "";
        }
    }

    /** 엑셀 업로드 후 DB 저장 */
    public List<String> storeContractList(List<ContractExcelDto> excelDtoList) {
      List<String> registerList = new ArrayList<>();
        for (ContractExcelDto excel : excelDtoList) {
            try {
                if (excel.isNewRow()) {
                    // 신규 등록
                    RegisterContractDto registerContractDto = new RegisterContractDto();

                    registerContractDto.setContractYear(excel.getContractYear());
                    registerContractDto.setContractNo(excel.getContractNo());
                    registerContractDto.setContractStartDate(excel.getContractStDt());
                    registerContractDto.setContractEndDate(excel.getContractEdDt());
                    registerContractDto.setDeptCd(excel.getDeptCd());
                    registerContractDto.setSubCd(excel.getSubCd());
                    registerContractDto.setRemark(excel.getRemark());
                    
                    contractService.registerContractInfo(registerContractDto);
                    
                } else if (excel.isUpdateRow()) {
                    UpdateContractDto updateDto = new UpdateContractDto();
                    
                    updateDto.setContractYear(excel.getContractYear());
                    updateDto.setContractNo(excel.getContractNo());
                    updateDto.setContractStartDate(excel.getContractStDt());
                    updateDto.setContractEndDate(excel.getContractEdDt());
                    updateDto.setDeptCd(excel.getDeptCd());
                    updateDto.setSubCd(excel.getSubCd());
                    updateDto.setRemark(excel.getRemark());
                    
                    contractService.updateContractInfo(updateDto);
                }
                
            } catch (Exception e) {
                log.error("사용자 저장 실패: {}", e);
                registerList.add(excel.getContractNo());
            }
        }
        return registerList;
    } 
}
