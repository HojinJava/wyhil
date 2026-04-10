package com.hnix.sd.common.department.service;

import com.hnix.sd.common.department.dao.DepartmentDao;
import com.hnix.sd.common.department.dto.manage.DeptRegisterDto;
import com.hnix.sd.common.department.dto.manage.DeptRemoveDto;
import com.hnix.sd.common.department.dto.manage.FailedDeptDto;
import com.hnix.sd.common.department.dto.DepartmentDto;
import com.hnix.sd.common.history.dao.CommonHistoryDao;
import com.hnix.sd.core.utils.MemberUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeptService {

    private final DepartmentDao departmentDao;
    private final DeptHistoryService deptHistoryService;
    private final CommonHistoryDao commonHistoryDao;
    private final MemberUtil memberUtil;


    @Transactional
    public List<FailedDeptDto> updateMultipleDepartment(List<DeptRegisterDto> registerList) {
        List<FailedDeptDto> failedList = new ArrayList<>();
        
        for (DeptRegisterDto dto : registerList) {
            try {
                DepartmentDto department = departmentDao.findById(dto.getDeptCd()).orElse(null);
                
                
                if (department == null) {
                    // 신규 등록    
                    department = new DepartmentDto();
                    department.setDeptCd(dto.getDeptCd());
                    department.setDeptNm(dto.getDeptNm());
                    department.setPrntDeptCd(dto.getPrntDeptCd());
                    department.setDeptTypeCd(dto.getDeptTypeCd());
                    department.setCompanyTypeCd(dto.getCompanyTypeCd());
                    department.setUseYn(dto.getUseYn());
                    department.setDeptDesc(dto.getDeptDesc());
                    department.setRegDt(LocalDateTime.now());
                    department.setRegId(memberUtil.getUserId());
                    
                    departmentDao.save(department);
                    deptHistoryService.createDeptHistory(department, dto);
                    
                } else {
                    // 수정 - History 추가       
                    deptHistoryService.addDeptHistory(department, dto);
                    
                    department.setDeptNm(dto.getDeptNm());
                    department.setPrntDeptCd(dto.getPrntDeptCd());
                    department.setDeptTypeCd(dto.getDeptTypeCd());
                    department.setCompanyTypeCd(dto.getCompanyTypeCd());
                    department.setUseYn(dto.getUseYn());
                    department.setDeptDesc(dto.getDeptDesc());
                    department.setModDt(LocalDateTime.now());
                    department.setModId(memberUtil.getUserId());
                    
                    departmentDao.save(department);
                }
                
            } catch (Exception e) {
                log.error("부서 정보 업데이트 실패 : {}", dto.getDeptCd(), e);
                failedList.add(new FailedDeptDto(dto.getDeptCd(), dto.getDeptNm()));
            }
        }
        
        return failedList;
    }


    @Transactional
    public void storeDepartment(DeptRegisterDto requestDto) {
        DepartmentDto department = departmentDao.findById(requestDto.getDeptCd()).orElse(null);

        if (department == null) {
            DepartmentDto requestedDept = new DepartmentDto();
            requestedDept.setDeptCd(requestDto.getDeptCd());
            requestedDept.setDeptNm(requestDto.getDeptNm());
            requestedDept.setDeptTypeCd(requestDto.getDeptTypeCd());
            requestedDept.setCompanyTypeCd(requestDto.getCompanyTypeCd());
            requestedDept.setDeptDesc(requestDto.getDeptDesc());
            requestedDept.setPrntDeptCd(requestDto.getPrntDeptCd());
            requestedDept.setCompClassCd(requestDto.getCompClassCd());
            requestedDept.setRegId(requestDto.getRegId());
            requestedDept.setRegDt(LocalDateTime.now());

            departmentDao.save(requestedDept);
            return;
        }

        department.setDeptCd(requestDto.getDeptCd());
        department.setDeptNm(requestDto.getDeptNm());
        department.setDeptTypeCd(requestDto.getDeptTypeCd());
        department.setCompanyTypeCd(requestDto.getCompanyTypeCd());
        department.setDeptDesc(requestDto.getDeptDesc());
        department.setPrntDeptCd(requestDto.getPrntDeptCd());
        department.setCompClassCd(requestDto.getCompClassCd());
        department.setModId(requestDto.getRegId());
        department.setModDt(LocalDateTime.now());

        departmentDao.save(department);
    }

    public Set<String> removeDepartments(DeptRemoveDto deptRemoveDto) {
        List<String> codes = deptRemoveDto.getDeptCds();
        Set<String> existUsers = checkBelongsUserFromDept(codes);

        if (!existUsers.isEmpty()) {
            return existUsers;
        }

        commonHistoryDao.deleteByMenuCdAndTargetIdIn("common-dept-department", codes);

        departmentDao.deleteByDeptCds(codes);

        return new HashSet<>();
    }

    public boolean checkDuplicateDeptCd(String deptCd) {
        return departmentDao.existsById(deptCd);
    }


    public Set<String> checkBelongsUserFromDept(List<String> deptCodes) {
        return departmentDao.findBelongsUserByDept(deptCodes);
    }

    public Set<String> checkMultipleDuplicateDeptCd(List<String> deptCodes) {
        return departmentDao.findExistingCodes(deptCodes);
    }

}
