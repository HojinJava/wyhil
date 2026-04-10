package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateWorkRegistrationDto {

    private String serviceNo;
    private String serviceCd;
    private String reqSupportCd;
    private String reqContents;
    private String procSupportCd;
    private String procContents;
    private String statusCd;
    private String remark;
    private String modId;

    private LocalDate reqDate;
    private LocalDate procDt;

}
