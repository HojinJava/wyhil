package com.hnix.sd.work.software.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContractDetailDto {

    private String contractYear;
    private String contractNo;
    private String subCd;
    private String customerCompanyNm;
    private String customerCompanyCd;
    private String customerDepartmentNm;
    private String customerDepartmentCd;
    private String customerManageNm;
    private String customerManageCd;
    private String partnerCompanyNm;
    private String partnerCompanyCd;
    private String swName;
    private String swCode;
    private String remark;
    private String partnerContractCd;
    private String historyTypeCd; 
    private LocalDateTime contractStDt;
    private LocalDateTime contractEdDt;
    private String regId;
    private LocalDateTime regDt;

}
