package com.hnix.sd.common.user.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistDto {

    private String userId;
    private String deptNm;
    private String deptCd;
    private String userNm;
    private String userDeptNm;
    private String userPw;
    private String userPositionNm;
    private String userEmail;
    private String userPhoneMobile;
    private String userPhoneOffice;
    private String remark;
    private String regId;
    private Character userCertYn;
    private Character userConsentYn;

    private String reqUserContractType;
    private String companyTypeCd;
    private List<String> mgrCompanyList; // 관리고객사
    private String mgrCompanyNm; // 관리고객사 이름

}
