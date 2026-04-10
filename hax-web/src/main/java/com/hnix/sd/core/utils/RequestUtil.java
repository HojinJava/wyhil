package com.hnix.sd.core.utils;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestUtil {

    
	public String getIp() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		String ip = request.getHeader("X-Forwarded-For");

		 if ( ipEmptyCheck(ip) ) {
		     ip = request.getHeader("Proxy-Client-IP");
		 }
		 if ( ipEmptyCheck(ip) ) {
		     ip = request.getHeader("WL-Proxy-Client-IP");
		 }
		 if ( ipEmptyCheck(ip) ) {
		     ip = request.getHeader("HTTP_CLIENT_IP");
		 }
		 if ( ipEmptyCheck(ip) ) {
		     ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		 }
		 if ( ipEmptyCheck(ip) ) {
		 	 ip = request.getHeader("X-FORWARDED-FOR");
		 }
		 if ( ipEmptyCheck(ip) ) {
		     ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		 }
		 if ( ipEmptyCheck(ip) ) {
		     ip = request.getRemoteAddr();
		 }

		 return ip;
	}

	private boolean ipEmptyCheck(String ip) {
		return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
	}

	
	public String getSessionId () {
		ServletRequestAttributes currentRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return (String) currentRequestAttributes.getRequest().getSession().getId();
	}

}
