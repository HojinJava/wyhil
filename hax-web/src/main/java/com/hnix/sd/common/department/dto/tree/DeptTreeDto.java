package com.hnix.sd.common.department.dto.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptTreeDto {

    private String deptCd;
    private String deptNm;
    private String prntDeptCd;
    private String prntDeptNm; // 상위부서 이름
    private Character deptTypeCd;
    private Character companyTypeCd;
    private String compClassCd;
    private String deptDesc;
    private Character useYn;
    private Integer level;

}
