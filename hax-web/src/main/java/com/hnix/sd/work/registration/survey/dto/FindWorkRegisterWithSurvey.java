package com.hnix.sd.work.registration.survey.dto;

import com.hnix.sd.work.registration.certificate.dto.CertificateWithUserDto;
import com.hnix.sd.work.registration.record.dto.FindWorkRegistrationDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FindWorkRegisterWithSurvey {

    private FindWorkRegistrationDto registration;

    private CertificateWithUserDto certificate;

    private SurveyInfoDto survey;

}
