package com.hnix.sd.common.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptTreeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "부서 코드는 필수 입력 값입니다.")
    private String deptCd;

    private String prntDeptCd;

    @NotNull
    private String deptNm;

    private Character deptTypeCd;

    private String compClassCd;

    private Character companyTypeCd;

    private String deptDesc;

    private Character useYn;

    private Integer level;
}
