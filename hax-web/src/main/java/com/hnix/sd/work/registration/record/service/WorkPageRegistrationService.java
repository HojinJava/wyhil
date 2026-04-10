package com.hnix.sd.work.registration.record.service;

import com.hnix.sd.common.auth.user.dao.UserAuthDao;
import com.hnix.sd.common.department.service.DepartmentStructureService;
import com.hnix.sd.common.excel.dto.WorkRegistrationExcelFieldsDto;
import com.hnix.sd.core.dto.PageRequestDto;
import com.hnix.sd.core.dto.PageResponseDto;
import com.hnix.sd.work.registration.record.dao.WorkRegistrationDao;
import com.hnix.sd.work.registration.record.dto.WorkRegisterGridDto;
import com.hnix.sd.work.registration.record.dto.WorkRegistrationSearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkPageRegistrationService {

    private final UserAuthDao userAuthDao;
    private final DepartmentStructureService departmentService;
    private final WorkRegistrationDao workRegistrationDao;

    @Transactional(readOnly = true)
    public List<WorkRegistrationExcelFieldsDto> getWorkRegistrationWithExcel(
        LocalDate startDate,
        LocalDate endDate,
        String customerCd,
        String departmentCd,
        String partnerCd,
        String loginUserId,
        String loginUserDeptCd,
        String satisfied,
        String softwareName,
        String subCode
    ) {
        WorkRegistrationSearchDto searchDto = createSearchDto(
            startDate, endDate, customerCd, departmentCd, partnerCd, satisfied, loginUserId, loginUserDeptCd, softwareName, subCode
        );
        return workRegistrationDao.findWorkRegistrationExcel(searchDto);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<WorkRegisterGridDto> getServiceInfoWithPagination(
        int page,
        int size,
        LocalDate startDate,
        LocalDate endDate,
        String customerCd,
        String departmentCd,
        String partnerCd,
        String satisfied,
        String loginUserId,
        String loginUserDept,
        String softwareName,
        String subCode,
        String sort
    ) {
        WorkRegistrationSearchDto searchDto = createSearchDto(
            startDate, endDate, customerCd, departmentCd, partnerCd, satisfied, loginUserId, loginUserDept, softwareName, subCode
        );
        
        searchDto.setPageNo(page);
        searchDto.setPageSize(size);
        
        // Handle sort string (e.g. "regDt,desc")
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            searchDto.setCriteria(parts[0]);
            searchDto.setSort(parts[1].toUpperCase());
        }

        List<WorkRegisterGridDto> content = workRegistrationDao.findWorkRegistrationPagination(searchDto);
        long total = workRegistrationDao.countWorkRegistrationPagination(searchDto);

        PageRequestDto pageRequest = PageRequestDto.builder()
            .page(page)
            .size(size)
            .build();

        return new PageResponseDto<>(content, pageRequest, total);
    }

    private WorkRegistrationSearchDto createSearchDto(
        LocalDate startDate, LocalDate endDate,
        String customerCd, String departmentCd, String partnerCd,
        String satisfied, String loginUserId, String loginUserDeptCd,
        String softwareName, String subCode
    ) {
        List<String> userAuths = userAuthDao.findByGroupCdWithUserId(loginUserId);
        String loginUserCompanyCd = departmentService.getDepartmentByDeptCd(loginUserDeptCd);

        WorkRegistrationSearchDto searchDto = new WorkRegistrationSearchDto();
        searchDto.setStartDate(startDate);
        searchDto.setEndDate(endDate);
        searchDto.setCustomerCd(customerCd);
        searchDto.setDepartmentCd(departmentCd);
        searchDto.setPartnerCd(partnerCd);
        searchDto.setSatisfied(satisfied);
        searchDto.setLoginUserId(loginUserId);
        searchDto.setLoginUserCompanyCd(loginUserCompanyCd);
        searchDto.setUserAuths(userAuths);
        searchDto.setSoftwareName(softwareName);
        searchDto.setSubCode(subCode);

        // Role flags for MyBatis
        searchDto.setAdmin(userAuths.contains("ADMIN"));
        searchDto.setManage(userAuths.contains("MANAGE"));
        searchDto.setDeptManage(userAuths.contains("DEPT_MANAGE"));

        return searchDto;
    }
}
