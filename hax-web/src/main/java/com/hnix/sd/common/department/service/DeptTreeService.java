package com.hnix.sd.common.department.service;

import com.hnix.sd.common.department.dao.DepartmentDao;
import com.hnix.sd.common.department.dto.tree.DeptTreeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DeptTreeService {

    private final DepartmentDao departmentDao;

    public List<DeptTreeDto> getTreeDataFromDepartment() {
        return departmentDao.findTreeByDeptCd(null).stream()
                .map(this::convertToTreeDto)
                .collect(Collectors.toList());
    }

    // 관리고객사
    public List<DeptTreeDto> getManagementDepartments() {
        return departmentDao.findTreeByCompanyTypeCd('C').stream()
                .map(this::convertToTreeDto)
                .collect(Collectors.toList());
    }

    // 상위부서 이름 추가
    public List<DeptTreeDto> getTreeDataFromprntDeptNm() {
        return departmentDao.findAllPrntDeptNm()
                .stream()
                .map(r -> {
                    DeptTreeDto dto = new DeptTreeDto();
                    dto.setDeptCd((String) r.get("deptCd"));
                    dto.setDeptNm((String) r.get("deptNm"));
                    dto.setPrntDeptCd((String) r.get("prntDeptCd"));
                    dto.setPrntDeptNm((String) r.get("prntDeptNm"));
                    dto.setDeptTypeCd(r.get("deptTypeCd") == null ? null : r.get("deptTypeCd").toString().charAt(0));
                    dto.setCompanyTypeCd(r.get("companyTypeCd") == null ? null : r.get("companyTypeCd").toString().charAt(0));
                    dto.setCompClassCd((String) r.get("compClassCd"));
                    dto.setDeptDesc((String) r.get("deptDesc"));
                    dto.setUseYn(r.get("useYn") == null ? null : r.get("useYn").toString().charAt(0));
                    dto.setLevel(r.get("level") == null ? 0 : ((Number) r.get("level")).intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DeptTreeDto> getTreeDataFromprntDeptNmByDeptCd(List<String> deptCd) {
        return departmentDao.findAllPrntDeptNmByDeptCd(deptCd)
                .stream()
                .map(r -> {
                    DeptTreeDto dto = new DeptTreeDto();
                    dto.setDeptCd((String) r.get("deptCd"));
                    dto.setDeptNm((String) r.get("deptNm"));
                    dto.setPrntDeptCd((String) r.get("prntDeptCd"));
                    dto.setPrntDeptNm((String) r.get("prntDeptNm"));
                    dto.setDeptTypeCd(r.get("deptTypeCd") == null ? null : r.get("deptTypeCd").toString().charAt(0));
                    dto.setCompanyTypeCd(r.get("companyTypeCd") == null ? null : r.get("companyTypeCd").toString().charAt(0));
                    dto.setCompClassCd((String) r.get("compClassCd"));
                    dto.setDeptDesc((String) r.get("deptDesc"));
                    dto.setUseYn(r.get("useYn") == null ? null : r.get("useYn").toString().charAt(0));
                    dto.setLevel(r.get("level") == null ? 0 : ((Number) r.get("level")).intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DeptTreeDto> getDepartmentParents(String deptCd) {
        return departmentDao.findTreeByDeptCd(deptCd).stream()
                .map(this::convertToTreeDto)
                .collect(Collectors.toList());
    }

    private DeptTreeDto convertToTreeDto(com.hnix.sd.common.department.dto.DeptTreeDto daoDto) {
        DeptTreeDto dto = new DeptTreeDto();
        dto.setDeptCd(daoDto.getDeptCd());
        dto.setDeptNm(daoDto.getDeptNm());
        dto.setPrntDeptCd(daoDto.getPrntDeptCd());
        dto.setDeptTypeCd(daoDto.getDeptTypeCd());
        dto.setCompanyTypeCd(daoDto.getCompanyTypeCd());
        dto.setCompClassCd(daoDto.getCompClassCd());
        dto.setDeptDesc(daoDto.getDeptDesc());
        dto.setUseYn(daoDto.getUseYn());
        dto.setLevel(daoDto.getLevel());
        return dto;
    }
}
