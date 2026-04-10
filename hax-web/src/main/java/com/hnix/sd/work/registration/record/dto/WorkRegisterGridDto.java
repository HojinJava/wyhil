package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WorkRegisterGridDto {

    private String serviceNo;
    private String contractNo;
    private String subCd;
    private String swCd;
    private String swNm;
    private String partnerCompanyNm;
    private String serviceCdNm;
    private String customerCompanyNm;
    private String customerDepartmentNm;
    private String customerManageNm;
    private LocalDateTime reqDt;
    private String reqCompCd;
    private String reqUserId;
    private String reqUserNm;
    private String userDeptNm;
    private String reqUserDepartmentNm;
    private String reqUserCompanyNm;
    private LocalDateTime procDt;
    private String procUserNm;
    private String procSupportCd;
    private String procSupportNm; //처리매체코드
    private String procCompanyNm;

    private String certCd;
    private LocalDateTime certDt;

    private Character pointDisYn;

    private String statusCd;
    private String statusNm;

    private String procResultCd;
    private String procResultNm;


}
