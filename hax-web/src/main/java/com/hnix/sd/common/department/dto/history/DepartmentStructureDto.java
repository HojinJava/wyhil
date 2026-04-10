package com.hnix.sd.common.department.dto.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStructureDto {

    private String originCd;
    private String originNm;
    private String companyCd;
    private String companyNm;
    private String departmentCd;
    private String departmentNm;
    private String manageCd;
    private String manageNm;
    private String prntDeptCd;
    private Character deptTypeCd;
    private String compClassCd;
    private Character compTypeCd;
    private Integer level;

}
