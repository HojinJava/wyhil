package com.hnix.sd.common.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserGridDto {

    private String userId;
    private String companyNm;
    private String departmentNm;
    private String manageNm;
    private String userDeptNm;
    private String deptCd;
    private String userNm;
    private String userEmail;
    private String userPositionNm;
    private String userPhoneOffice;
    private String userPhoneMobile;
    private String remark;
    private Character userCertYn;
    private Character userConsentYn;
    private LocalDateTime regDt;
    private String regId;

    private Character deleteYn;
    private String mgrCompanyNm;
    private LocalDateTime lastAccessDt;
    private String authGroup;
}
