package com.hnix.sd.work.software.contract.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractSearchDto {

    private String contractYear;
    private String customerCode = "";
    private String manageDeptCode = "";
    private String partnerCode = "";
    private String swNm = "";
    private String subCode = "";
    private String deptCd = "";
    private String compContract = "";
    private Integer pageNo = 0;
    private Integer pageSize = 10;
    private String sortBy = "";
    private String partnerContractNo = "";
    private String remark = "";
    private String partnerContractCd = "";
    private String hisTypeCd = "";

    private int offset;
    private int limit;
    private String hCode = "";
    private String contractNo = "";
}
