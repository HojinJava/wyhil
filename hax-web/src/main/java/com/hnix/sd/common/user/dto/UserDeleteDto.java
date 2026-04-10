package com.hnix.sd.common.user.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeleteDto {
	private String userId;
    private String userEmail;
    private String userPhoneMobile;
    private String userPhoneOffice;
    private Character userCertYn;
    private Character userConsentYn;
    private Character deleteYn;
    private String userCheckCd;
    private String modId;

	private List<String> userIds; // 다중선택
}
