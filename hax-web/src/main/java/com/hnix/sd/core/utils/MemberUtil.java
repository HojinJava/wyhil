package com.hnix.sd.core.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnix.sd.core.constant.ComConstants;
import com.hnix.sd.common.login.dto.MemberDto;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class MemberUtil {



    public MemberDto getMember() {

        ServletRequestAttributes currentRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String jsonData = (String) currentRequestAttributes.getRequest().getSession().getAttribute(ComConstants.SESSION_KEY_NAME);

		System.out.println("# jsonData : " + jsonData);
		//jsonData : {"userId":"dawnseo@naver.com","deptCd":"COMPANY3_DEPT0","userNm":"서동희","userPositionNm":"","userEmail":"dawnseo@naver.com","deptNm":null,"sessionId":null}
		//jsonData : null //세션 종료된 경우
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        
        MemberDto memberDto = null;
		try {
			memberDto = objectMapper.readValue(jsonData, MemberDto.class);
		}
		catch (Exception e) {}

        return memberDto;
    }




    public String getUserId() {
        if(getMember() == null) return "";
        return getMember().getUserId();
    }



    public String getJson(HttpServletRequest request) {
        String json = (String) request.getSession().getAttribute(ComConstants.SESSION_KEY_NAME);
        return json;
    }

}
