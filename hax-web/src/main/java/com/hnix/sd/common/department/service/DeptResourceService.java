package com.hnix.sd.common.department.service;

import com.hnix.sd.common.department.dao.DepartmentDao;
import com.hnix.sd.common.department.dto.DepartmentDto;
import com.hnix.sd.common.department.dto.manage.DeptInfoDto;
import com.hnix.sd.common.department.dto.search.DeptWithCompanyDto;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DeptResourceService {

    private final DepartmentDao departmentDao;

    public DeptInfoDto getDepartmentFromCode(String deptCd) {
        Optional<Object[]> department = departmentDao.findByDeptFromCode(deptCd);

        if (department.isEmpty() || department.get().length < 2) {
            return new DeptInfoDto();
        }

        Object[] existDept = department.get();
        String company = (String) existDept[0];
        DepartmentDto dept = (DepartmentDto) existDept[1];

        return toDeptInfoDTO(company, dept);
    }

    public List<DeptInfoDto> getDeptWithCompanyName() {
        return departmentDao.findDeptWithCompanyName()
                .stream()
                .map(d -> toDeptInfoDTO((String) d[0], (DepartmentDto) d[1]))
                .collect(Collectors.toList());
    }

    public List<DeptInfoDto> getCompanyList(Character compTypeCd) {
        List<DepartmentDto> companyEntities = departmentDao.findByCompany();

        if (compTypeCd != null && compTypeCd != ' ') {
            return companyEntities
                    .stream()
                    .filter(d -> d.getCompanyTypeCd() != null && d.getCompanyTypeCd().equals(compTypeCd))
                    .map(this::toDeptInfoDTO)
                    .collect(Collectors.toList());
        }

        return companyEntities
                .stream()
                .map(this::toDeptInfoDTO)
                .collect(Collectors.toList());
    }

    public DeptInfoDto getCompanyFromCode(String deptCd) {
        DepartmentDto dept = departmentDao.findByDeptCd(deptCd);
        return dept != null ? toDeptInfoDTO(dept) : new DeptInfoDto();
    }

    public List<DeptInfoDto> getDepartmentListFromCompany(String deptCd) {
        return departmentDao.findByDeptWithCompany(deptCd)
                .stream()
                .map(this::toDeptInfoDTO)
                .collect(Collectors.toList());
    }

    /** 회사 & 부서 공통 Dialog에서 사용하는 Service */
    public List<DeptWithCompanyDto> getDepartmentListAll(Character compTypeCd) {
        List<Object[]> searchData = departmentDao.findAllDepartment(compTypeCd);
        return convertEntityToDeptWithCompany(searchData);
    }

    public List<DeptWithCompanyDto> searchDeptFromKeyword(String keyword) {
        List<Object[]> searchData = departmentDao.findByDeptFromKeyword(keyword);
        return convertEntityToDeptWithCompany(searchData);
    }

    public List<DeptWithCompanyDto> convertEntityToDeptWithCompany(List<Object[]> searchData) {
        return searchData.stream()
                .map(info -> {
                    String company = (String) info[0];
                    DepartmentDto dept = (DepartmentDto) info[1];
                    return toDeptWithCompany(company, dept);
                })
                .collect(Collectors.toList());
    }

    private DeptInfoDto toDeptInfoDTO(String company, DepartmentDto dept) {
        if (dept == null) return new DeptInfoDto();
        DeptInfoDto dto = new DeptInfoDto();
        dto.setCompany(company);
        dto.setDeptCd(dept.getDeptCd());
        dto.setDeptNm(dept.getDeptNm());
        dto.setDeptTypeCd(dept.getDeptTypeCd());
        dto.setCompanyTypeCd(dept.getCompanyTypeCd());
        dto.setDeptDesc(dept.getDeptDesc());
        dto.setPrntDeptCd(dept.getPrntDeptCd());
        dto.setCompClassCd(dept.getCompClassCd());
        dto.setUseYn(dept.getUseYn());
        return dto;
    }

    private DeptInfoDto toDeptInfoDTO(DepartmentDto dept) {
        return toDeptInfoDTO(null, dept);
    }

    private DeptWithCompanyDto toDeptWithCompany(String company, DepartmentDto dept) {
        if (dept == null) return new DeptWithCompanyDto();
        DeptWithCompanyDto dto = new DeptWithCompanyDto();
        dto.setCompany(company);
        dto.setDeptCd(dept.getDeptCd());
        dto.setDeptNm(dept.getDeptNm());
        dto.setPrntDeptCd(dept.getPrntDeptCd());
        dto.setDeptTypeCd(dept.getDeptTypeCd());
        dto.setCompanyTypeCd(dept.getCompanyTypeCd());
        dto.setCompClassCd(dept.getCompClassCd());
        return dto;
    }
}
