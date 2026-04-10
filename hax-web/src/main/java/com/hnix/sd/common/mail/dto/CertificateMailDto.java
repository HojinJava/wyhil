package com.hnix.sd.common.mail.dto;

import lombok.Getter;


@Getter
public class CertificateMailDto {

    private String serviceNo;
    private String partner;
    private String softwareName;
    private String userEmail;
    private String sendTo;
    private String toCc;
    private String subCode;
    private String contractNo;
}
