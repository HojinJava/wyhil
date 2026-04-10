package com.hnix.sd.common.excel.service;


import com.hnix.sd.common.excel.dto.ExcelField;
import com.hnix.sd.common.excel.dto.WorkResultExcelFieldsDto;
import com.hnix.sd.common.excel.dto.WorkResultExportParams;
import com.hnix.sd.common.excel.util.WorkCellStyle;
import com.hnix.sd.common.excel.util.WorkExcelCommon;
import com.hnix.sd.common.excel.util.WorkSheetLayout;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import static com.hnix.sd.common.excel.util.ExcelFormat.CELL;


@RequiredArgsConstructor
@Service
public class WorkResultExcelExportService implements WorkExcelCommon {

	private final com.hnix.sd.common.excel.dao.ExcelDao excelDao;
	private final com.hnix.sd.common.auth.user.dao.UserAuthDao userAuthDao;
	private final com.hnix.sd.common.department.service.DepartmentStructureService departmentService;

	public SXSSFWorkbook workResultExcelExport(WorkResultExportParams search) {
		java.util.Map<String, Object> queryParams = new java.util.HashMap<>();
		queryParams.put("startDate", search.getStartDate());
		queryParams.put("endDate", search.getEndDate());
		queryParams.put("customerCd", search.getCustomerCd());
		queryParams.put("departmentCd", search.getDepartmentCd());
		queryParams.put("partnerCd", search.getPartnerCd());
		queryParams.put("loginUserId", search.getUserId());

		List<String> userAuths = userAuthDao.findByGroupCdWithUserId(search.getUserId());
		String loginUserCompanyCd = departmentService.getDepartmentByDeptCd(search.getDeptCd());

		queryParams.put("loginUserCompanyCd", loginUserCompanyCd);
		queryParams.put("isAdmin", userAuths.contains("ADMIN"));
		queryParams.put("isManage", userAuths.contains("MANAGE"));
		queryParams.put("isDeptManage", userAuths.contains("DEPT_MANAGE"));
		queryParams.put("satisfied", search.getSatisfied());
		queryParams.put("softwareName", search.getSoftwareName());
		queryParams.put("subCode", search.getSubCode());

		List<WorkResultExcelFieldsDto> results = excelDao.getWorkResultExcel(queryParams);

		List<ExcelField> fields = new ArrayList<>();
		fields.add(new ExcelField("subCd", "SUB CODE", ALIGN.LEFT, 3000));
		fields.add(new ExcelField("swName", "소프트웨어", ALIGN.LEFT, 3500));
		fields.add(new ExcelField("serviceCdNm", "서비스항목", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("reqDt", "요청일자", ALIGN.CENTER, 3500));
		fields.add(new ExcelField("customerCompanyNm", "고객사", ALIGN.LEFT, 5000));
		fields.add(new ExcelField("customerDepartmentNm", "사업부", ALIGN.LEFT, 4500));
		fields.add(new ExcelField("customerManageNm", "관리부서", ALIGN.LEFT, 4500));

		fields.add(new ExcelField("reqUserCompanyNm", "요청자회사", ALIGN.CENTER, 3000)); //추가
		fields.add(new ExcelField("reqUserDepartmentNm", "요청자사업부", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("userDeptNm", "요청자 부서", ALIGN.LEFT, 4500));
		fields.add(new ExcelField("reqUserNm", "요청자", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("reqContents", "접수내용", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("procDt", "처리일자", ALIGN.CENTER, 3500));

		//fields.add(new ExcelField("procCompanyNm", "협력사", ALIGN.LEFT, 4500));
		fields.add(new ExcelField("partnerCompanyNm", "협력사", ALIGN.LEFT, 4500));

		fields.add(new ExcelField("procUserNm", "처리자", ALIGN.CENTER, 7000));
		fields.add(new ExcelField("procSupportNm", "처리매체", ALIGN.CENTER, 5000));
		fields.add(new ExcelField("procContents", "지원내역", ALIGN.CENTER, 7000));
		fields.add(new ExcelField("statusNm", "처리상태", ALIGN.CENTER, 3500));
		fields.add(new ExcelField("remark", "비고", ALIGN.CENTER, 7000));

		//fields.add(new ExcelField("certDt", "승인일자", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("certCdNm", "동의여부", ALIGN.CENTER, 7000));
		fields.add(new ExcelField("certComment", "의견", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("pointDisYn", "불만족여부", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("pointSum", "만족도 점수", ALIGN.CENTER, 3000));
		fields.add(new ExcelField("serviceNo", "서비스번호", ALIGN.LEFT, 4500));
		fields.add(new ExcelField("contractNo", "계약번호", ALIGN.LEFT, 9000));

		return createWorkResultExcelFile(results, fields);
	}


	@Override
	public SXSSFWorkbook createWorkResultExcelFile(List<?> results, List<ExcelField> fields) {
		/* Set sheet layout */
		SXSSFWorkbook workbook = new SXSSFWorkbook();

		SXSSFSheet sheet = WorkSheetLayout.createSheetLayout(workbook, "작업처리현황", fields);

		/* set sheet body style*/
		Font bodyFont = WorkCellStyle.setCellFont(workbook.createFont(), 230, false);

		CellStyle bodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.CENTER, true, false, CELL.BODY);
		CellStyle alignBodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.LEFT, true, false, CELL.BODY_ALIGN);

		int startRowIndex = 4;

		for (var gridRowData : results) {
			SXSSFRow row = sheet.createRow(startRowIndex++);

			List<String> rowData = getFieldValues(gridRowData);

			for (int i = 0; i < fields.size(); i++) {
				ExcelField field = fields.get(i);
				String val = rowData.get(i);

				SXSSFCell cell = row.createCell(i + 1);
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
		WorkResultExcelFieldsDto dto = (WorkResultExcelFieldsDto) data;

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

		String certYn = "아니오";
		if (dto.getCertDt() != null) {
			LocalDate certDate = dto.getCertDt().toLocalDate();
			certDtStr = certDate.format(format);
			certYn = "예";
		}

		if (dto.getCertCd() != null) {
			String certCd = dto.getCertCd();
			if(certCd.equalsIgnoreCase("WORK_CERT_REJECT")) certCdStr = "반려";
			if(certCd.equalsIgnoreCase("WORK_CERT_APPROVAL")) certCdStr = "동의";
			if(certCd.equalsIgnoreCase("WORK_STATUS_SAVE")) certCdStr = "";
		}


		String pointDisYnStr = (dto.getPointDisYn() == null) ? "" : (dto.getPointDisYn() == 'Y') ? "만족" : "불만족";
		String pointSumStr = dto.getPointSum() == null || dto.getPointSum() == 0 ? "" : dto.getPointSum().toString();

		return Arrays.asList(
			dto.getSubCd(),
			dto.getSwName(),
			dto.getServiceCdNm(),
			reqDtStr,
			dto.getCustomerCompanyNm(),
			dto.getCustomerDepartmentNm(),
			dto.getCustomerManageNm(),
			dto.getReqUserCompanyNm(),
			dto.getReqUserDepartmentNm(),
			dto.getUserDeptNm(),
			dto.getReqUserNm(),
			dto.getReqContents(),
			procDtStr,
			//dto.getProcCompanyNm(),
			dto.getPartnerCompanyNm(),
			dto.getProcUserNm(),
			dto.getProcSupportNm(),
			dto.getProcContents(),
			dto.getStatusNm(),
			dto.getRemark(),
			certCdStr,
			dto.getCertComment(),
			pointDisYnStr,
			pointSumStr,
			dto.getServiceNo(),
			dto.getContractNo()
		);
	}


	@Override
	public XSSFWorkbook createWorkResultExcelFile2(List<?> results, List<ExcelField> fields) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'createWorkResultExcelFile2'");
	}

}
