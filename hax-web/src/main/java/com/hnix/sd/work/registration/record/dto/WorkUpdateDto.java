package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkUpdateDto {

    private String serviceNo;
    private String contractNo;
    private String subCd;
    private String serviceCd;

    private LocalDate reqDate;
    private String reqCompCd;
    private String reqUserId;
    private String reqSupportCd;
    private String reqContents;
    private String statusCode;

    private LocalDate procDt;
    private String procUserNm;
    private String procSupportCd;
    private String procContents;
    private String procResultCd;
    private String remark;
    private String regId;

}
