package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateContractDto {

    private String contractNo;
    private String subCd;
    private String deptCd;
    private String swName;
    private String customerCompanyNm;
	private String customerDepartmentNm;
	private String customerManageNm;
	private String partnerCompanyNm;
	private String partnerContractCd;
    private String contractYear;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;
    private String remark;
    private String modId;
    private String regId;

}
