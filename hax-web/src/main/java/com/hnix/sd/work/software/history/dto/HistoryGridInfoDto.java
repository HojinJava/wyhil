package com.hnix.sd.work.software.history.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryGridInfoDto {

    private String historyId;
    private String contractNo;
    private String contractYear;
    private String subCd;
    private String swName;
    private String customerCompanyNm;
    private String customerDepartmentNm;
    private String customerManageNm;
    private String customerDeptCd;
    private String companyNm;
    private String partnerCompanyNm;
    private String partnerDeptCd;
    private String manageNm;
    private Integer historySeq;
    private String historyTypeCd;
    private String historyContents;

}
