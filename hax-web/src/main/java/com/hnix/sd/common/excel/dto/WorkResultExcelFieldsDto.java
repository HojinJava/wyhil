package com.hnix.sd.common.excel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WorkResultExcelFieldsDto {

    private String serviceNo;
    private String contractNo;
    private String subCd;
    private String swName;
    private String serviceCdNm;
    private LocalDateTime reqDt;
    private String reqCompCd;
    private String customerCompanyNm;
    private String customerDepartmentNm;
    private String customerManageNm;
    private String reqUserId;
    private String reqUserNm;
    private String reqContents;
    private String certCdNm;
    private String certCd;
    private LocalDateTime procDt;
    private String procUserNm;
    private String procContents;
    private String procResultCd;
    private String remark;
    private LocalDateTime certDt;
    private String certComment;
    private Character pointDisYn;
    private Integer pointSum;

    private String procCompanyNm;

    private String statusNm;
    private String procSupportNm;
    private String partnerCompanyNm;
    private String reqUserCompanyNm;
    private String reqUserDepartmentNm;
    private String userDeptNm;


}
