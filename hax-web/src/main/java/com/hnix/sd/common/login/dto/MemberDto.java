package com.hnix.sd.common.login.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

	private String userId;
	private String deptCd;
	private String userNm;
	private String userDeptNm;
	private String userPositionNm;
	private String userEmail;
	private String deptNm;
	private Character deleteYn;
	private Character userCertYn;
	private Character userConsentYn;
	private String sessionId;
	
}
