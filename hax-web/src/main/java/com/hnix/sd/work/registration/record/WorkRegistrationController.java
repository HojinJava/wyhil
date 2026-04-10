package com.hnix.sd.work.registration.record;


import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.registration.record.dto.UpdateWorkRegistrationDto;
import com.hnix.sd.work.registration.record.dto.WorkRegisterDto;
import com.hnix.sd.work.registration.record.service.WorkPageRegistrationService;
import com.hnix.sd.work.registration.record.service.WorkRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Work Registration Controller", description = "작업 등록 현황 조회 컨트롤러")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/work/registration")
@RestController
public class WorkRegistrationController {

	private final WorkRegistrationService registrationService;
	private final WorkPageRegistrationService pageRegistrationService;
	private final ComResponseUtil comResponseUtil;


	@Operation(summary = "계약 등록")
	@PostMapping("/multi/register")
	public ComResponseDto<?> registerMultiWorkContents(@RequestBody WorkRegisterDto registerDto) {
		return comResponseUtil.setResponse200ok( registrationService.registerMultiWorkContents(registerDto) );
	}

	@Operation(summary = "작업 등록 현황 조회")
	@GetMapping("/page")
	public ComResponseDto<?> getServiceInfoWithPagination(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int size,
			@RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(name = "companyCd", defaultValue = "") String customer,
			@RequestParam(name = "departmentCd", defaultValue = "") String department,
			@RequestParam(name = "partnerCd", defaultValue = "") String partner,
			@RequestParam(name = "satisfied", defaultValue = "") String satisfied,
			@RequestParam(name = "userId", defaultValue = "") String userId,
			@RequestParam(name = "deptCd", defaultValue = "") String deptCd,
			@RequestParam(name = "softwareName", defaultValue = "") String softwareName,
			@RequestParam(name = "subCode", defaultValue = "") String subCode,
			@RequestParam(name = "sortBy", defaultValue = "") String sort) {

		log.info("# /multi/register/page run");
		return comResponseUtil.setResponse200ok(pageRegistrationService.getServiceInfoWithPagination(page, size, startDate, endDate, customer, department, partner, satisfied, userId, deptCd, softwareName, subCode, sort) );
	}

	@Operation(summary = "작업 등록 정보 한 건 조회")
	@GetMapping("/{serviceNo}")
	public ComResponseDto<?> getWorkRegistrationFromServiceNo(@PathVariable("serviceNo") String serviceNo) throws NotFoundException {
		return comResponseUtil.setResponse200ok( registrationService.getWorkRegistrationFromServiceNo(serviceNo) );
	}

	@Operation(summary = "작업 등록 정보 업데이트")
	@PostMapping("/update/history")
	public ComResponseDto<?> updateServiceInfoByServiceNo(@RequestBody UpdateWorkRegistrationDto updateDto) throws NotFoundException {
		registrationService.updateServiceInfoByServiceNo(updateDto);
		return comResponseUtil.setResponse200ok();
	}
}
