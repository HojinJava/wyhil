package com.hnix.sd.common.login.service;

import com.hnix.sd.common.login.dto.LoginReqDto;
import com.hnix.sd.common.login.dto.MemberDto;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.hnix.sd.core.utils.SecurtyUtil.sha512;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final UserDao userDao;

	public MemberDto login(LoginReqDto loginReqDto) {

		UserDto user = userDao.findByUserEmail(loginReqDto.getUserEmail()).orElseGet(UserDto::new);

		if (user.getUserId() == null) {
			throw new BizException("userNotFound");
		}

		String encryptPw = sha512(loginReqDto.getUserPw());

		if (!user.getUserPw().equals(encryptPw)) {
			throw new BizException("loginFail");
		}

        MemberDto dto = new MemberDto();
        dto.setUserId(user.getUserId());
        dto.setUserNm(user.getUserNm());
        dto.setUserEmail(user.getUserEmail());
        dto.setDeptCd(user.getDeptCd());
        dto.setUserDeptNm(user.getUserDeptNm());
        dto.setUserPositionNm(user.getUserPositionNm());
        dto.setDeleteYn(user.getDeleteYn());
        dto.setUserCertYn(user.getUserCertYn());
        dto.setUserConsentYn(user.getUserConsentYn());

		return dto;
	}

	public void createAdmin() {
        UserDto admin = new UserDto();
        admin.setUserId("admin");
        admin.setDeptCd("HNIX001");
        admin.setUserNm("Admin");
        admin.setUserDeptNm("Company");
        admin.setUserPw(sha512("1234!@#$"));
        admin.setUserEmail("admin@hncorp.world");
        admin.setUserCertYn('N');
        admin.setUserConsentYn('N');
        admin.setDeleteYn('N');
        admin.setRegDt(LocalDateTime.now());
        admin.setRegId("SYSTEM");
		userDao.save(admin);
	}

}
