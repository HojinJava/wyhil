package com.hnix.sd.common.department.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptWithCompanyDto {

    private String company;
    private String deptCd;
    private String deptNm;
    private String prntDeptCd;
    private Character deptTypeCd;
    private Character companyTypeCd;
    private String compClassCd;

}
