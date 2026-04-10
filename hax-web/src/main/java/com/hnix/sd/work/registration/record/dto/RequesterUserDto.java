package com.hnix.sd.work.registration.record.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequesterUserDto {

    private String userId;
    private String userNm;
    private String userPositionNm;
    private String userEmail;
    private String userPhoneOffice;
    private String userPhoneMobile;
    private String deptCd;
    private String userDeptNm;
    private String companyNm;
    private String departmentNm;
    private String manageNm;
    private Character compTypeCd;

}
