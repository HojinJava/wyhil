package com.hnix.sd.common.excel.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WorkResultExportParams {

    private LocalDate startDate;
    private LocalDate endDate;
    private String customerCd;
	private String departmentCd;
    private String partnerCd;
    private String userId;
    private String deptCd;
    private String satisfied;
	private String softwareName;
	private String subCode;
}
