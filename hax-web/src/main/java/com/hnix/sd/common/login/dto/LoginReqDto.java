package com.hnix.sd.common.login.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginReqDto {

	private String userId;
	private String userEmail; // 20251117 로그인 이메일 파라미터 추가
	private String userPw;

}
