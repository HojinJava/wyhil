package com.hnix.sd.common.department.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "부서 코드는 필수 입력 값입니다.")
    private String deptCd;

    @NonNull
    private String deptNm;

    private Character deptTypeCd;

    private String deptDesc;

    private String prntDeptCd;

    private String compClassCd;

    private Character companyTypeCd;

    private Character useYn;

    private String regId;

    private String modId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;

    public Character getDeptTypeCd() {
        return deptTypeCd != null ? deptTypeCd : 'Y';
    }

    public Character getUseYn() {
        return useYn != null ? useYn : 'Y';
    }
}
