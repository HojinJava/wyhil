package com.hnix.sd.work.registration.record.dto;

import com.hnix.sd.common.user.dto.UserRegistDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class WorkRegisterDto {

    private String contractNo;
    private String subCd;
    private String serviceCd;
    private String serviceNo;

    private LocalDate reqDate;
    private List<UserRegistDto> requester;
    private String reqSupportCd;
    private String reqContents;
    private String reqCompCd;
    private String statusCode;

    private LocalDate procDt;
    private String procUserNm;
    private String procSupportCd;
    private String procContents;
    private String procDeptCd;
    private String procResultCd;

    private String remark;
    private String regId;

}
