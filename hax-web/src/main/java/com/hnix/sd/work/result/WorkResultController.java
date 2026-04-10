package com.hnix.sd.work.result;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.result.dto.SearchWorkResultDto;
import com.hnix.sd.work.result.service.WorkResultService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

//work/result/page/
@RequiredArgsConstructor
@RequestMapping("/work/result")
@RestController
public class WorkResultController {

	private final WorkResultService resultService;
	private final ComResponseUtil comResponseUtil;

	@Operation(summary = "작업 결과 목록 조회")
	@GetMapping("/page")
	public ComResponseDto<?> getWorkResultText(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int size,
			@RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(name = "customerCd", defaultValue = "") String customer,
			@RequestParam(name = "departmentCd", defaultValue = "") String department,
			@RequestParam(name = "partnerCd", defaultValue = "") String partner,
			@RequestParam(name = "userId", defaultValue = "") String userId,
			@RequestParam(name = "deptCd", defaultValue = "") String deptCd,
			@RequestParam(name = "satisfied", defaultValue = "N") String satisfied,
			@RequestParam(name = "softwareName", defaultValue = "") String softwareName,
			@RequestParam(name = "subCode", defaultValue = "") String subCode,
			@RequestParam(name = "sortBy", defaultValue = "") String sort) {
		return comResponseUtil.setResponse200ok( resultService.getWorkResultPagination(startDate, endDate, customer, department, partner, userId, deptCd, satisfied, page, size, softwareName, subCode, sort) );
	}

}
