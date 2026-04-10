package com.hnix.sd.work.software.contract.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hnix.sd.common.department.dto.DepartmentDto;
import com.hnix.sd.work.software.partner.dto.PartnerDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractExcelDto {
  private String contractYear;
	private String subCd;
	private String swName;
	private String customerCompanyNm;
	private String customerDepartmentNm;
	private String customerManageNm;
	private String deptCd;
	private String partnerCompanyNm;
	private String partnerContractCd;
  private LocalDateTime contractStDt;
  private LocalDateTime contractEdDt;
	private String contractStDtStr;
	private String contractEdDtStr;
  private String contractNo;
  private String remark;

	private boolean newRow;        // 신규 여부
	private boolean updateRow;     // 수정 여부
	private String errorMsg;       // 검증 오류 메시지

	public ContractExcelDto(String contractYear, String subCd, String swName, String customerCompanyNm, String customerDepartmentNm, String customerManageNm, String deptCd, String partnerCompanyNm, String partnerContractCd, LocalDateTime contractStDt, LocalDateTime contractEdDt, String contractNo, String remark) {
		this.contractYear = contractYear;
    this.subCd = subCd;
		this.swName = swName;
		this.customerCompanyNm = customerCompanyNm;
		this.customerDepartmentNm = customerDepartmentNm;
		this.customerManageNm = customerManageNm;
    this.deptCd = deptCd;
		this.partnerCompanyNm = partnerCompanyNm;
		this.partnerContractCd = partnerContractCd;
    this.contractStDt = contractStDt;
    this.contractEdDt = contractEdDt;
    this.contractNo = contractNo;
    this.remark = remark;
	}

  public String validate(PartnerDto partner, DepartmentDto department) {
		String msg = "";

		if(contractYear.length() < 4 || contractYear.length() > 4) {
			msg +="계약년도가 4자리가 아닙니다. / ";
		}

		if(subCd == null || subCd.trim().isEmpty()) {
			msg +="소프트웨어 협력사 코드가 비어있습니다. / ";
		} else if (partner == null) {
			msg +="SUB CODE가 소프트웨어 협력사 관리에 존재하지 않습니다. / ";
		}

    if(deptCd == null || deptCd.trim().isEmpty()) {
			msg +="관리부서가 비어있습니다. / ";
		} else if(department == null) {
			msg +="관리부서CODE가 회사 부서관리에 존재하지 않습니다. / ";
		}

    if (contractStDtStr == null || contractStDtStr.trim().isEmpty()) {
			msg += "시작일 날짜가 비어있습니다. / ";
		} else {
				try {
						this.contractStDt = LocalDateTime.parse(contractStDtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				} catch (Exception e) {
						msg += "시작일 날짜 형식이 올바르지 않습니다.(yyyy-MM-dd HH:mm:ss) / ";
				}
		}
		
		// 종료일 검증 및 파싱 (수정된 부분)
		if (contractEdDtStr == null || contractEdDtStr.trim().isEmpty()) {
				msg += "종료일 날짜가 비어있습니다. / ";
		} else {
				try {
						this.contractEdDt = LocalDateTime.parse(contractEdDtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				} catch (Exception e) {
						msg += "종료일 날짜 형식이 올바르지 않습니다.(yyyy-MM-dd HH:mm:ss) / ";
				}
		}

    if(contractNo == null || contractNo.trim().isEmpty()) {
			msg +="계약번호가 비어있습니다. / ";
		}

		return msg;
	}


}
