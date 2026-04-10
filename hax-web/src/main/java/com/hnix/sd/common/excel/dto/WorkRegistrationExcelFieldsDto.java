package com.hnix.sd.common.excel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class WorkRegistrationExcelFieldsDto {

    private String serviceNo;
    private String contractNo;
    private String subCd;
    private String swNm;
    private String serviceCdNm;
    private String customerCompanyNm;
    private String customerDepartmentNm;
    private String customerManageNm;
    private String reqContents;
    private LocalDateTime reqDt;
    private String reqUserNm;
    private LocalDateTime procDt;
    private String procUserNm;
    private String procCompanyNm;
    private String procContents;
    private String certCd;
    private LocalDateTime certDt;
    private Character pointDisYn;

    private String statusNm;
    private String procSupportNm;
    private String partnerCompanyNm;
    private String reqUserCompanyNm;
    private String reqUserDepartmentNm;
    private String userDeptNm;





}
