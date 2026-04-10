package com.hnix.sd.common.department.service;

import com.hnix.sd.common.department.dao.DepartmentDao;
import com.hnix.sd.common.department.dto.DepartmentStructureDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentStructureService {

    private final DepartmentDao departmentDao;

    private com.hnix.sd.common.department.dto.history.DepartmentStructureDto toHistoryDto(DepartmentStructureDto d) {
        if (d == null) return null;
        com.hnix.sd.common.department.dto.history.DepartmentStructureDto dto = new com.hnix.sd.common.department.dto.history.DepartmentStructureDto();
        dto.setOriginCd(d.getOriginCd());
        dto.setOriginNm(d.getOriginNm());
        dto.setCompanyCd(d.getCompanyCd());
        dto.setCompanyNm(d.getCompanyNm());
        dto.setDepartmentCd(d.getDepartmentCd());
        dto.setDepartmentNm(d.getDepartmentNm());
        dto.setManageCd(d.getManageCd());
        dto.setManageNm(d.getManageNm());
        return dto;
    }

    public List<com.hnix.sd.common.department.dto.history.DepartmentStructureDto> getManageWithDepartment() {
        return departmentDao.findByDeptTypeNotCompany()
                .stream()
                .map(this::toHistoryDto)
                .collect(Collectors.toList());
    }

    public List<com.hnix.sd.common.department.dto.history.DepartmentStructureDto> getDepartmentHistoryFromDeptType(Character typeCd) {
        return departmentDao.findByDeptTypeCd(typeCd)
                .stream()
                .flatMap(d -> Stream.of(toHistoryDto(d)))
                .collect(Collectors.toList());
    }

    public List<com.hnix.sd.common.department.dto.history.DepartmentStructureDto> getManageDeptFromCompanyCd(String companyCd, Character deptTypeCd) {
        return departmentDao.findByDeptFromCompanyCd(companyCd)
                .stream()
                .map(this::toHistoryDto)
                .collect(Collectors.toList());
    }

    public String getDepartmentByDeptCd(String deptCd) {
        return departmentDao.findByCompanyCdFromDeptCd(deptCd).orElseGet(String::new);
    }

}
