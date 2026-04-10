package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContractSimpleInfoDto {

    private String contractNo;
    private String contractYear;
    private String subCd;
    private String deptCd; // 고객사
    private String deptNm;
    private String company; // 고객사
    private String compCd;
    private String compNm; // 파트너
    private String swCd;
    private String swNm;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;

}
