package com.hnix.sd.common.log.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccessLogInfoDto {

    private String companyNm;
    private String deptNm;
    private String manageNm;
    private String menuNm;
    private String menuLink;
    private String userNm;
    private String userId;
    private String menuCd;
    private LocalDateTime accessDt;
    private String menuType;
    private String userSessionId;
    private String userIp;

}
