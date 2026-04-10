package com.hnix.sd.common.auth.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserInfoDto {

    private String groupCd;
    private Character userTypeCd;
    private String userId;
    private String userNm;
    private String userDeptNm;
    private String deptCd;
    private String deptNm;
    private String company;

}
