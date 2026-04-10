package com.hnix.sd.common.department.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStructureDto implements Serializable {

    private static final long serialVersionUID = 1L;

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
