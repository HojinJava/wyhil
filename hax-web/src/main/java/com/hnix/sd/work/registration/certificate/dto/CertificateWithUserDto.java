package com.hnix.sd.work.registration.certificate.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CertificateWithUserDto {

    private String serviceNo;
    private String userId;
    private String userNm;
    private LocalDateTime certDt;
    private String certCd;
    private String companyNm;
    private String departmentNm;
    private String manageNm;
    private String deptCd;
    private String certComment;
    private String regId;

}
