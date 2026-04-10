package com.hnix.sd.common.user.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangeUserInfoDto {

    private String userId;
    private String userEmail;
    private String userDeptNm;
    private String userPhoneMobile;
    private String userPhoneOffice;
    private String userPositionNm;
    private String remark;
    private String modId;

    private String companyTypeCd;
    private List<String> mgrCompanyList; // 관리고객사
    private String mgrCompanyNm; // 관리고객사 이름
    private String deptCd;
    private Character  userCertYn;
    private Character  userConsentYn;
}
