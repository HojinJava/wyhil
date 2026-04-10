package com.hnix.sd.common.auth.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptAuthInfoDto {

    private String deptCd;
    private String deptNm;
    private String company;
    private String groupCd;
    private Character userTypeCd;

}
