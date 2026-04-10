package com.hnix.sd.work.registration.record.dao;

import com.hnix.sd.work.registration.record.dto.WorkRegisterGridDto;
import com.hnix.sd.work.registration.record.dto.WorkRegistrationSearchDto;
import com.hnix.sd.common.excel.dto.WorkRegistrationExcelFieldsDto;
import com.hnix.sd.work.registration.record.dto.FindWorkRegistrationDto;
import com.hnix.sd.work.registration.record.dto.UpdateWorkRegistrationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface WorkRegistrationDao {

    List<WorkRegisterGridDto> findWorkRegistrationPagination(WorkRegistrationSearchDto searchDto);

    long countWorkRegistrationPagination(WorkRegistrationSearchDto searchDto);

    List<WorkRegistrationExcelFieldsDto> findWorkRegistrationExcel(WorkRegistrationSearchDto searchDto);

    FindWorkRegistrationDto findById(String serviceNo);

    java.util.Set<String> findByServiceNoStartingWith(String prefix);

    FindWorkRegistrationDto findWorkRegistrationByServiceNo(String serviceNo);

    void insertHistory(FindWorkRegistrationDto history);

    void updateHistory(FindWorkRegistrationDto history);

    void insertWorkRegistration(FindWorkRegistrationDto workReg);

    void updateWorkRegistration(FindWorkRegistrationDto workReg);

    void saveAll(@Param("workRegistrations") List<FindWorkRegistrationDto> workRegistrations);

    void updateServiceInfoByServiceNo(UpdateWorkRegistrationDto updateDto);

    void insertSubRegister(String serviceNo);

    default FindWorkRegistrationDto save(FindWorkRegistrationDto dto) {
        if (findById(dto.getServiceNo()) == null) {
            insertWorkRegistration(dto);
        } else {
            updateWorkRegistration(dto);
        }
        return dto;
    }
}
