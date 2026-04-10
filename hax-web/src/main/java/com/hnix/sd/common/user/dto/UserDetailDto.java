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
public class UserDetailDto {

    private String userId;
    private String userNm;
    private String userEmail;
    private String userDeptNm;
    private String deptCd;
    private String companyNm;
    private String companyCd;
    private String departmentNm;
    private String manageNm;
    private String userPositionNm;
    private String userPhoneOffice;
    private String userPhoneMobile;
    private Character userCertYn;
    private Character userConsentYn;
    private Character deleteYn;
    private String mgrCompanyNm; 
    private List<String> mgrCompanyList;
    private String remark;
}
