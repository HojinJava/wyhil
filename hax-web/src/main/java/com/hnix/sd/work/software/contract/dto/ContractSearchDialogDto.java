package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;

@Getter
public class ContractSearchDialogDto {

    private String contractYear;
    private String customerName = "";
    private String departmentName = "";
    private String partnerName = "";
    private String softwareName = "";
    private String subCode = "";
    private String loginUserId = "";
    private String loginDeptCd = "";

}
