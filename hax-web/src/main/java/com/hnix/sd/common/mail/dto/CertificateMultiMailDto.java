package com.hnix.sd.common.mail.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CertificateMultiMailDto {

    private String subCode;
    private String contractNo;
    private String reqDt;
    private String reqContents;
    private String softwareName;
    private String partner;
    private List<CertificateMailSendToDto> sendTo;
    private String toCc;

}
