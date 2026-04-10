package com.hnix.sd.common.department.dto.manage;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptExcelDto {

	private String prntDeptNm; // 상위부서
	private String prntDeptCd; // 상위부서코드
	private String deptCd; // 부서코드
	private String deptNm; // 부서
	private String deptTypeCd; // 부서구분코드
	private String deptTypeNm; // 부서구분	
	private String compClassCd; //계약관계
	private String compTypeCd; // 회사구분코드
	private String compTypeNm; // 회사구분
	private String useYn; // 사용여부
	private String deptDesc; // 설명

	private boolean newRow;        // 신규 여부
	private boolean updateRow;     // 수정 여부
	private String errorMsg;       // 검증 오류 메시지

    public String validate(Function<Character, Boolean> deptTypeCodeValidator, 
	Function<Character, Boolean> compTypeCodeValidator) {
		String msg = "";

		if(prntDeptCd == null || prntDeptCd.trim().isEmpty()) {
			msg +="상위부서CODE가 입력되지 않았습니다. / ";
		}
		if(deptNm == null || deptNm.trim().isEmpty()) {
			msg +="부서가 입력되지 않았습니다. / ";
		}
		if(deptCd == null || deptCd.trim().isEmpty()) {
			msg +="부서CODE가 입력되지 않았습니다. / ";
		}else if(deptCd.length() > 100) {
			msg +="부서CODE가 100자를 초과했습니다. / ";
		}
		if(deptTypeCd == null || deptTypeCd.trim().isEmpty()) {
			msg +="부서구분CODE가 입력되지 않았습니다. / ";
		} else if (deptTypeCodeValidator != null && !deptTypeCodeValidator.apply(deptTypeCd.charAt(0))) {
			msg +="부서구분CODE가 존재하지 않습니다. / ";
		}
		if(compTypeCd == null || compTypeCd.trim().isEmpty()) {
			msg +="회사구분CODE가 입력되지 않았습니다. / ";
		} else if (compTypeCodeValidator != null && !compTypeCodeValidator.apply(compTypeCd.charAt(0))) {
			msg +="회사구분CODE가 존재하지 않습니다. / ";
		}
        if(useYn == null || useYn.trim().isEmpty()) {
            msg +="사용여부가 입력되지 않았습니다.";
        }else if(!(useYn.equalsIgnoreCase("Y") || useYn.equalsIgnoreCase("N"))) {
			msg +="사용여부는 Y 또는 N만 입력해야 합니다. ";
		}

		return msg;
	}
}
