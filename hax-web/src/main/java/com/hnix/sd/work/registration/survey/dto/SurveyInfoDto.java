package com.hnix.sd.work.registration.survey.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SurveyInfoDto {

    private String serviceNo;
    private String userId;
    private Integer pointQui;
    private Integer pointAcc;
    private Integer pointFri;
    private Integer pointSat;
    private Integer pointSum;
    private Character pointDisYn;
    private String surveyComment;
    private LocalDateTime surveyDt;
    private String regId;

}
