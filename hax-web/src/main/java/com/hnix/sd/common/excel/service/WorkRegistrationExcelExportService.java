package com.hnix.sd.common.excel.service;

import com.hnix.sd.common.excel.dto.ExcelField;
import com.hnix.sd.common.excel.dto.ReportSubcodeExcelParams;
import com.hnix.sd.common.excel.dto.WorkRegistrationExcelFieldsDto;
import com.hnix.sd.common.excel.dto.WorkRegistrationExportParams;
import com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import com.hnix.sd.common.excel.util.ExcelFormat.CELL;
import com.hnix.sd.common.excel.util.WorkCellStyle;
import com.hnix.sd.common.excel.util.WorkExcelCommon;
import com.hnix.sd.common.excel.util.WorkSheetLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import static com.hnix.sd.common.excel.util.ExcelFormat.CELL;


@Slf4j
@RequiredArgsConstructor
@Service
public class WorkRegistrationExcelExportService implements WorkExcelCommon {

    private final com.hnix.sd.common.excel.dao.ExcelDao excelDao;
    private final com.hnix.sd.common.auth.user.dao.UserAuthDao userAuthDao;
    private final com.hnix.sd.common.department.service.DepartmentStructureService departmentService;


    public SXSSFWorkbook workRegistrationExcelExport(WorkRegistrationExportParams params) {

        java.util.Map<String, Object> queryParams = new java.util.HashMap<>();
        queryParams.put("startDate", params.getStartDate());
        queryParams.put("endDate", params.getEndDate());
        queryParams.put("customerCd", params.getCustomerCd());
        queryParams.put("departmentCd", params.getDepartmentCd());
        queryParams.put("partnerCd", params.getPartnerCd());
        queryParams.put("loginUserId", params.getUserId());
        
        List<String> userAuths = userAuthDao.findByGroupCdWithUserId(params.getUserId());
        String loginUserCompanyCd = departmentService.getDepartmentByDeptCd(params.getDeptCd());
        
        queryParams.put("loginUserCompanyCd", loginUserCompanyCd);
        queryParams.put("isAdmin", userAuths.contains("ADMIN"));
        queryParams.put("isManage", userAuths.contains("MANAGE"));
        queryParams.put("isDeptManage", userAuths.contains("DEPT_MANAGE"));
        queryParams.put("satisfied", params.getSatisfied());
        queryParams.put("softwareName", params.getSoftwareName());
        queryParams.put("subCode", params.getSubCode());

        List<WorkRegistrationExcelFieldsDto> results = excelDao.getWorkRegistrationExcel(queryParams);

        List<ExcelField> fields = new ArrayList<>();
        fields.add(new ExcelField("subCd", "SUB CODE", ALIGN.LEFT, 3000));
        fields.add(new ExcelField("swNm", "소프트웨어", ALIGN.LEFT, 3500));
        fields.add(new ExcelField("serviceCdNm", "서비스항목", ALIGN.CENTER, 3000));
        fields.add(new ExcelField("reqDt", "요청일자", ALIGN.CENTER, 3500));

        fields.add(new ExcelField("customerCompanyNm", "고객사", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("customerDepartmentNm", "사업부", ALIGN.LEFT, 4500));
        fields.add(new ExcelField("customerManageNm", "관리부서", ALIGN.LEFT, 4500));

        fields.add(new ExcelField("reqUserCompanyNm", "요청자회사", ALIGN.CENTER, 3000));
        fields.add(new ExcelField("reqUserDepartmentNm", "요청자사업부", ALIGN.CENTER, 3000));
        fields.add(new ExcelField("userDeptNm", "요청자부서", ALIGN.CENTER, 3000));
        fields.add(new ExcelField("reqUserNm", "요청자", ALIGN.CENTER, 3000));
        fields.add(new ExcelField("procCompanyNm", "협력사", ALIGN.LEFT, 4500));
        fields.add(new ExcelField("procUserNm", "처리자", ALIGN.CENTER, 5000));
        fields.add(new ExcelField("procSupportNm", "처리매체", ALIGN.CENTER, 5000));
        fields.add(new ExcelField("statusNm", "처리상태", ALIGN.CENTER, 3500));
        fields.add(new ExcelField("procDt", "처리일자", ALIGN.CENTER, 3500));
        fields.add(new ExcelField("certCd", "동의여부", ALIGN.CENTER, 3500));
        fields.add(new ExcelField("certDtStr", "승인일자", ALIGN.CENTER, 3500));
        fields.add(new ExcelField("pointDisYn", "서비스만족도", ALIGN.CENTER, 3000));
        fields.add(new ExcelField("serviceNo", "서비스번호", ALIGN.LEFT, 4500));
        fields.add(new ExcelField("contractNo", "계약번호", ALIGN.LEFT, 9000));

        return createWorkResultExcelFile(results, fields);
    }


    @Override
    public SXSSFWorkbook createWorkResultExcelFile(List<?> results, List<ExcelField> fields) {

        System.out.println("# createWorkResultExcelFile() start");

        SXSSFWorkbook workbook = new SXSSFWorkbook();

        System.out.println("# 83");

        SXSSFSheet sheet = WorkSheetLayout.createSheetLayout(workbook, "작업등록현황", fields);

        Font bodyFont = WorkCellStyle.setCellFont(workbook.createFont(), 230, false);

        CellStyle bodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.CENTER, true, false, CELL.BODY);
        CellStyle alignBodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.LEFT, true, false, CELL.BODY_ALIGN);

        System.out.println("# 92");

        int startRowIndex = 4;

        for (var gridRowData : results) {

            System.out.println("# 98");

            SXSSFRow row = sheet.createRow(startRowIndex++);

            List<String> rowData = getFieldValues(gridRowData);

            System.out.println("# fields.size() : " + fields.size());

            for (int i = 0; i < fields.size(); i++) {

                System.out.println("# 106");

                ExcelField field = fields.get(i);
                String val = rowData.get(i);

                SXSSFCell cell = row.createCell(i + 1);

                cell.setCellValue(val == null || val.equals("null") ? "" : val);
                cell.setCellStyle(field.getAlign() == ALIGN.CENTER ? bodyCellStyle : alignBodyCellStyle);
            }
        }

        if (!results.isEmpty()) {

            System.out.println("# 120");

            final int filterFirstRow = 4;
            final int filterLastRow = results.size() - 1 + filterFirstRow;

            sheet.setAutoFilter(CellRangeAddress.valueOf(String.format("B%d:P%d", filterFirstRow, filterLastRow)));
        }

        System.out.println("# createWorkResultExcelFile() end");

        return workbook;
    }






    @Override
    public List<String> getFieldValues(Object data) {
        WorkRegistrationExcelFieldsDto dto = (WorkRegistrationExcelFieldsDto) data;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String reqDtStr = "";
        String procDtStr = "";
        String certDtStr = "";
        String certCdStr = "";

        if (dto.getReqDt() != null) {
            LocalDate reqDate = dto.getReqDt().toLocalDate();
            reqDtStr = reqDate.format(format);
        }

        if (dto.getProcDt() != null) {
            LocalDate procDate = dto.getProcDt().toLocalDate();
            procDtStr = procDate.format(format);
        }

        if (dto.getCertDt() != null) {
            LocalDate certDate = dto.getCertDt().toLocalDate();
            certDtStr = certDate.format(format);
        }

        if (dto.getCertCd() != null) {
            String certCd = dto.getCertCd();
            if(certCd.equalsIgnoreCase("WORK_CERT_REJECT")) certCdStr = "반려";
            if(certCd.equalsIgnoreCase("WORK_CERT_APPROVAL")) certCdStr = "동의";
            if(certCd.equalsIgnoreCase("WORK_STATUS_SAVE")) certCdStr = "";
        }


        String pointDisYnStr = dto.getPointDisYn() == null || dto.getPointDisYn() == 'N' ? "아니오" : "예";

        return Arrays.asList(
                dto.getSubCd(),
                dto.getSwNm(),
                dto.getServiceCdNm(),
                reqDtStr,
                dto.getCustomerDepartmentNm(),
                dto.getCustomerManageNm(),
                dto.getPartnerCompanyNm(),
                dto.getReqUserCompanyNm(),
                dto.getReqUserDepartmentNm(),
                dto.getUserDeptNm(),
                dto.getReqUserNm(),
                dto.getProcCompanyNm(),
                dto.getProcUserNm(),
                dto.getProcSupportNm(),
                dto.getStatusNm(),
                procDtStr,
                certCdStr,
                certDtStr,
                pointDisYnStr,
                dto.getServiceNo(),
                dto.getContractNo(),
                dto.getCustomerCompanyNm()

        );
    }


    @Override
    public XSSFWorkbook createWorkResultExcelFile2(List<?> results, List<ExcelField> fields) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createWorkResultExcelFile2'");
    }

    public XSSFWorkbook regMultiExcelExport() throws IOException {
        InputStream fis = new ClassPathResource("template/HAX-WEB_작업일괄등록_양식.xlsx").getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        fis.close();
        return workbook;
    }
    
}
