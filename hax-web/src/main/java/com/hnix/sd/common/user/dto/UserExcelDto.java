package com.hnix.sd.common.user.dto;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExcelDto {
	private String companyNm; // 회사
	private String departmentNm; // 사업부
	private String userDeptNm; // 부서
	private String deptCd; // 부서코드
	private String userPositionNm; // 직책
	private String userNm; // 이름
	private String userId; // 아이디
	private String userEmail; // 이메일
	private String userPhoneMobile; // 휴대전화
	private String userPhoneOffice; // 사무실전화
	private Character userCertYn; // 사용자 인증
    private Character userConsentYn; // 개인정보 동의
	private Character deleteYn; // 삭제 여부
    private String mgrCompanyNm; // 관리고객사
    private LocalDateTime lastAccessDt; // 마지막 접속일
    private String authGroup; // 권한 그룹
	private String remark; // 비고

	private boolean newRow;        // 신규 여부
	private boolean updateRow;     // 수정 여부
	private String errorMsg;       // 검증 오류 메시지

	public String validate(Function<String, Boolean> deptCodeValidator) {
		String msg = "";

		if(deptCd == null || deptCd.trim().isEmpty()) {
			msg +="부서CODE가 입력되지 않았습니다. / ";
		} else if (deptCodeValidator != null && !deptCodeValidator.apply(deptCd)) {
			msg +="부서CODE가 존재하지 않습니다. / ";
		}
		if(userNm == null || userNm.trim().isEmpty()) {
			msg +="사용자 이름이 입력되지 않았습니다. / ";
		}
		if(userId == null || userId.trim().isEmpty()) {
			msg +="사용자 ID가 입력되지 않았습니다. / ";
		}
		if(userEmail == null || userEmail.trim().isEmpty()) {
			msg +="사용자 이메일이 입력되지 않았습니다. / ";
		} else {
			// 이메일 형식 체크
			String emailRegex = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
			boolean isEmailValid = Pattern.matches(emailRegex, userEmail);
			if (!isEmailValid) {
				msg += "사용자 이메일 형식이 올바르지 않습니다. ";
			}
		}

		return msg;
	}
}