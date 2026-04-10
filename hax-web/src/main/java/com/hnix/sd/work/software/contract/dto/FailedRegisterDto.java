package com.hnix.sd.work.software.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailedRegisterDto {

    private String subCd;
    private String deptCd;
    private String reason;

}
