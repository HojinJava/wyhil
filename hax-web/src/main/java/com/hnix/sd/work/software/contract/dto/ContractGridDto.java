package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContractGridDto {

    private String contractYear;
    private String contractNo;
    private String subCd;
    private String compCd;
    private String compNm;
    private String deptCd;
    private String company;
    private String deptNm;
    private String swCd;
    private String swNm;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;
    private String regId;
    private LocalDateTime regDt;
    private String modId;
    private LocalDateTime modDt;
    private String remark;

}
