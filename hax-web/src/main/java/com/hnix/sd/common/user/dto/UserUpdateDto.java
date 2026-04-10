package com.hnix.sd.common.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private String userId;
    private String deptCd;
    private String userDeptNm;
    private String userPositionNm;
    private String userPhoneMobile;
    private String userPhoneOffice;
    private String remark;
}
