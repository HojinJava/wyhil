package com.hnix.sd.common.department.dto.manage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptInfoDto {

    private String company;
    private String deptCd;
    private String deptNm;
    private Character deptTypeCd;
    private Character companyTypeCd;
    private String deptDesc;
    private String prntDeptCd;
    private String compClassCd;
    private Character useYn;

}

