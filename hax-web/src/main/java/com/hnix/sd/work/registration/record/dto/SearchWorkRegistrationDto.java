package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SearchWorkRegistrationDto {

    private Integer pageNo = 0;
    private Integer pageSize = 10;
    private String criteria = "regDt";
    private String sort = "DESC";
    private LocalDate startDate;
    private LocalDate endDate;
    private String companyCd;
    private String departmentCd;
    private String partnerCd;
    private String satisfied;

}
