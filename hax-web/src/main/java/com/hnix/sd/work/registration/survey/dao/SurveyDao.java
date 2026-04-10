package com.hnix.sd.work.registration.survey.dao;

import com.hnix.sd.work.registration.survey.dto.SurveyInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SurveyDao {
    
    SurveyInfoDto findById(String serviceNo);
    
    void insertSurvey(SurveyInfoDto survey);
    
    void updateSurvey(SurveyInfoDto survey);
    
    void saveAll(@Param("surveys") List<SurveyInfoDto> surveys);
    
    default SurveyInfoDto save(SurveyInfoDto dto) {
        if (findById(dto.getServiceNo()) == null) {
            insertSurvey(dto);
        } else {
            updateSurvey(dto);
        }
        return dto;
    }
}
