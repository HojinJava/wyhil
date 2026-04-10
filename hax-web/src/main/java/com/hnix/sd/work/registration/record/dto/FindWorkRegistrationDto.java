package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FindWorkRegistrationDto {

    private String serviceNo = "";
    private String contractNo = "";
    private String subCd = "";
    private String swCode = "";
    private String swName = "";
    private String serviceCd = "";
    private String serviceCdNm = "";

    /* Requester: 요청자 */
    private LocalDateTime reqDt;
    private String reqUserId = "";
    private String reqUserNm = "";
    private String reqDeptCd = "";
    private String requestCompanyNm = "";
    private String requestDepartmentNm = "";
    private String requestManageNm = "";
    private String reqSupportCd = "";
    private String reqSupportCdNm = "";
    private String reqContents = "";
    private String reqUserContractType = "";
    private String statusCd = "";

    /* 계약 관리부서 */
    private String contractCompanyNm;
    private String contractDepartmentNm;
    private String contractManageNm;

    /* Partner: 계약한 협력사 */
    private LocalDateTime procDt;
    private String partnerDeptCd = "";
    private String partnerCompanyNm = "";

    /* Process: 처리자 (작업 등록자) */
    private String procSupportCd = "";
    private String procSupportCdNm = "";
    private String procUserNm = "";
    private String procCompanyNm = "";
    private String procCompanyNm2 = "";
    private String procContents = "";
    private String procResultCd = "";
    private String remark = "";

    private String regId = "";
    private LocalDateTime regDt;
    private String modId = "";
    private LocalDateTime modDt;

}
