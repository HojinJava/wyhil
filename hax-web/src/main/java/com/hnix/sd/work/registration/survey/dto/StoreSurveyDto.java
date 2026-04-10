package com.hnix.sd.work.registration.survey.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreSurveyDto {

    private String serviceNo;
    private String userId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime surveyDt;

    private Integer pointQui;
    private Integer pointAcc;
    private Integer pointFri;
    private Integer pointSat;
    private Integer pointSum;
    private Character pointDisYn;
    private String surveyComment;
    private String regId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;

    private String modId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;

    public Character getUserCertYn() {
        return pointDisYn != null ? pointDisYn : 'N';
    }
}
