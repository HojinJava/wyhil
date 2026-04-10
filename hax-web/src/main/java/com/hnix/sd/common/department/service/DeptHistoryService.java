package com.hnix.sd.common.department.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hnix.sd.common.department.dto.DepartmentDto;
import com.hnix.sd.common.code.dto.SubCodeDto;
import com.hnix.sd.common.code.service.CodeService;
import com.hnix.sd.common.history.CommonHistoryUtil;
import com.hnix.sd.common.history.dto.CommonHistoryDto;
import com.hnix.sd.common.department.dto.manage.DeptRegisterDto;
import com.hnix.sd.common.history.service.CommonHistoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DeptHistoryService {
	private final CommonHistoryService commonHistoryService;
	private final CodeService codeService;

	public void createDeptHistory(DepartmentDto department, DeptRegisterDto requestDto) {
		String menuCd = "common-dept-department";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_CREATE;
		String msg = "";
	
		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(requestDto.getDeptCd());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(requestDto.getRegId());
	
		commonHistoryService.addCommonHistory(commonHistoryDto);
	}
	
	public void addDeptHistory(DepartmentDto department, DeptRegisterDto requestDto) {
		String menuCd = "common-dept-department";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_UPDATE;

		String msg = "";

		if (CommonHistoryUtil.isOtherValue(department.getDeptNm(), requestDto.getDeptNm())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Name", department.getDeptNm(), requestDto.getDeptNm());
		}

		if (CommonHistoryUtil.isOtherValue(department.getDeptTypeCd(), requestDto.getDeptTypeCd())) {
			List<SubCodeDto> deptTypeList = codeService.getSubCodeFromGroupCodeCd("DEPT_TYPE");
			Map<String, String> deptTypeMap = deptTypeList.stream()
					.collect(Collectors.toMap(
						SubCodeDto::getCodeVal,
						code -> codeService.getSubCodeNameByCodeVal("DEPT_TYPE", code.getCodeVal())
					));

			String oldDeptTypeName = deptTypeMap.get(String.valueOf(department.getDeptTypeCd()));
			String newDeptTypeName = deptTypeMap.get(String.valueOf(requestDto.getDeptTypeCd()));
			msg = CommonHistoryUtil.getCommonHistory(msg, "Dept Type", oldDeptTypeName, newDeptTypeName);
		}

		if (CommonHistoryUtil.isOtherValue(department.getCompanyTypeCd(), requestDto.getCompanyTypeCd())) {
			List<SubCodeDto> compTypeList = codeService.getSubCodeFromGroupCodeCd("COMP_TYPE");
			Map<String, String> compTypeMap = compTypeList.stream()
					.collect(Collectors.toMap(
						SubCodeDto::getCodeVal,
						code -> codeService.getSubCodeNameByCodeVal("COMP_TYPE", code.getCodeVal())
					));

			String oldCompTypeName = compTypeMap.get(String.valueOf(department.getCompanyTypeCd()));
			String newCompTypeName = compTypeMap.get(String.valueOf(requestDto.getCompanyTypeCd()));
			msg = CommonHistoryUtil.getCommonHistory(msg, "Company Type", oldCompTypeName, newCompTypeName);
		}

		if (CommonHistoryUtil.isOtherValue(department.getUseYn(), requestDto.getUseYn())) {
			msg = CommonHistoryUtil.getCommonHistory(msg, "Use Yn",
				String.valueOf(department.getUseYn()), String.valueOf(requestDto.getUseYn()));
		}

		if (CommonHistoryUtil.isOtherValue(department.getDeptDesc(), requestDto.getDeptDesc())) {
			msg = CommonHistoryUtil.getCommonHistory(msg, "Description", department.getDeptDesc(), requestDto.getDeptDesc());
		}

		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(requestDto.getDeptCd());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(requestDto.getRegId());

		commonHistoryService.addCommonHistory(commonHistoryDto);
	}
}
