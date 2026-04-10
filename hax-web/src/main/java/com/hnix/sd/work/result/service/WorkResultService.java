package com.hnix.sd.work.result.service;

import com.hnix.sd.common.auth.user.dao.UserAuthDao;
import com.hnix.sd.common.department.service.DepartmentStructureService;
import com.hnix.sd.common.excel.dto.WorkResultExcelFieldsDto;
import com.hnix.sd.core.dto.PageRequestDto;
import com.hnix.sd.core.dto.PageResponseDto;
import com.hnix.sd.work.result.dao.WorkResultDao;
import com.hnix.sd.work.result.dto.SearchWorkResultDto;
import com.hnix.sd.work.result.dto.WorkResultGridDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@RequiredArgsConstructor
@Service
public class WorkResultService {

	private final UserAuthDao userAuthDao;
	private final DepartmentStructureService departmentService;
	private final WorkResultDao workResultDao;

	public List<WorkResultExcelFieldsDto> getWorkResultWithExcel(
		LocalDate startDate,
		LocalDate endDate,
		String customerCompanyCd,
		String departmentCd,
		String partnerCompanyCd,
		String userId,
		String loginUserDeptCd,
		String satisfied,
		String softwareName,
		String subCode
	) {
		List<String> userAuthCds = userAuthDao.findByGroupCdWithUserId(userId);
		final String companyCd = departmentService.getDepartmentByDeptCd(loginUserDeptCd);

		SearchWorkResultDto searchDto = new SearchWorkResultDto();
		searchDto.setStartDate(startDate);
		searchDto.setEndDate(endDate);
		searchDto.setCustomerCompanyCd(customerCompanyCd);
		searchDto.setDepartmentCd(departmentCd);
		searchDto.setPartnerCompanyCd(partnerCompanyCd);
		searchDto.setUserId(userId);
		searchDto.setLoginUserDeptCd(loginUserDeptCd);
		searchDto.setSatisfied(satisfied);
		searchDto.setSoftwareName(softwareName);
		searchDto.setSubCode(subCode);
		searchDto.setUserAuthCds(userAuthCds);
		searchDto.setCompanyCd(companyCd);

		return workResultDao.findWorkResultWithExcel(searchDto);
	}

	public PageResponseDto<WorkResultGridDto> getWorkResultPagination(
		LocalDate startDate,
		LocalDate endDate,
		String customerCompanyCd,
		String departmentCd,
		String partnerCompanyCd,
		String loginUserId,
		String loginUserDept,
		String satisfied,
		int page,
		int size,
		String softwareName,
		String subCode,
		String sort
	) {
		List<String> userAuthCds = userAuthDao.findByGroupCdWithUserId(loginUserId);
		final String companyCd = departmentService.getDepartmentByDeptCd(loginUserDept);

		SearchWorkResultDto searchDto = new SearchWorkResultDto();
		searchDto.setStartDate(startDate);
		searchDto.setEndDate(endDate);
		searchDto.setCustomerCompanyCd(customerCompanyCd);
		searchDto.setDepartmentCd(departmentCd);
		searchDto.setPartnerCompanyCd(partnerCompanyCd);
		searchDto.setUserId(loginUserId);
		searchDto.setLoginUserDeptCd(loginUserDept);
		searchDto.setSatisfied(satisfied);
		searchDto.setSoftwareName(softwareName);
		searchDto.setSubCode(subCode);
		searchDto.setUserAuthCds(userAuthCds);
		searchDto.setCompanyCd(companyCd);
		searchDto.setPageNo(page);
		searchDto.setPageSize(size);
		searchDto.setSort(sort);

		List<WorkResultGridDto> content = workResultDao.findWorkResultPagination(searchDto);
		long total = workResultDao.countWorkResultPagination(searchDto);

		PageRequestDto pageRequest = PageRequestDto.builder()
			.page(page)
			.size(size)
			.sort(sort)
			.build();

		return new PageResponseDto<>(content, pageRequest, total);
	}
}
