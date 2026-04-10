package com.hnix.sd.work.registration.survey.dto;

import com.hnix.sd.work.registration.certificate.dto.StoreCertificateDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreSurveyAndCertDto {

    private StoreSurveyDto survey;
    private StoreCertificateDto certificate;

}
