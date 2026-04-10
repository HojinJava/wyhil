package com.hnix.sd.work.result.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SearchWorkResultDto {

    private Integer pageNo = 0;
    private Integer pageSize = 10;
    private String criteria = "regDt";
    private String sort = "DESC";
    private LocalDate startDate;
    private LocalDate endDate;
    private String customerCompanyCd;
    private String departmentCd;
    private String partnerCompanyCd;
    private String userId;
    private String loginUserDeptCd;
    private String satisfied;
    private String softwareName;
    private String subCode;
    private List<String> userAuthCds;
    private String companyCd;
}
