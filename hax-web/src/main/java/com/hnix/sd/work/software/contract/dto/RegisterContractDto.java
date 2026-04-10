package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegisterContractDto {

    private String subCd;
    private String deptCd;
    private String contractYear;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;
    private String regId;

    private String contractNo;
    private String remark;

}
