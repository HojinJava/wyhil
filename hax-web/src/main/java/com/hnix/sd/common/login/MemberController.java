package com.hnix.sd.common.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnix.sd.common.login.dto.MemberDto;
import com.hnix.sd.common.login.service.MemberService;
import com.hnix.sd.core.utils.MemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.hnix.sd.core.constant.ComConstants;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.common.login.dto.LoginReqDto;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.ComResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;

@Slf4j
@Tag(name = "Member Controller", description = "User Login Management")
@RequiredArgsConstructor
@RequestMapping(value = "/member" )
@RestController
public class MemberController {

	private final MemberService memberService;
	private final MemberUtil memberUtil;

    private final ComResponseUtil comResponseUtil;


	//세션 타임아웃 설정
	@Value("${server.servlet.session.timeout:3600}")
	private int sessionTimeout;

	@PostMapping("/session")
	public ComResponseDto<?> getSessionInfo(HttpServletRequest req) {

		HashMap <String, String> map = new HashMap<String, String>();
		map.put("getLastAccessedTime", String.valueOf(req.getSession().getLastAccessedTime()));
		map.put("getMaxInactiveInterval", String.valueOf(req.getSession().getMaxInactiveInterval()));
		map.put("currentTimeMillis", String.valueOf(System.currentTimeMillis()));

		return comResponseUtil.setResponse200ok(map);
	}

	//member/login
	@Operation(summary = "User Login")
	@PostMapping("/login")
	public ComResponseDto<?> login (HttpServletRequest req, @RequestBody LoginReqDto loginReqDto) throws JsonProcessingException {

		MemberDto loginUserInfo = memberService.login(loginReqDto);

		if ( !loginUserInfo.getUserId().isEmpty()) {
			req.getSession(true).setAttribute(ComConstants.SESSION_KEY_NAME, this.deSerializationFeature(loginUserInfo));
			req.getSession().setMaxInactiveInterval(sessionTimeout);

			log.info("Get user id ::: {}, {}", memberUtil.getUserId(), loginUserInfo.getUserId());

			loginUserInfo.setSessionId( req.getSession().getId() );
		}
		else {
			throw new BizException("loginFail");
		}
		
		return comResponseUtil.setResponse200ok(loginUserInfo);
	}

	@Operation(summary = "사용자 로그아웃")
	@DeleteMapping("/logout")
	public ComResponseDto<?> login (HttpServletRequest req){
		req.getSession().removeAttribute(ComConstants.SESSION_KEY_NAME);
		req.getSession().invalidate();
		return comResponseUtil.setResponse200ok();
	}

	public String deSerializationFeature(MemberDto member) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

		return objectMapper.writeValueAsString(member);
	}

	/*
	@GetMapping("/login/hax-web/start")
	public ComResponseDto<?> signIn() {
		memberService.createAdmin();
		return comResponseUtil.setResponse200ok();
	}
	*/

}
