package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractSimpleSearchDto {

    private String contractYear;
    private String customer;
    private String department; // 고객사 관리부서명
    private String partner;
    private String software;
    private String subCode;

}
