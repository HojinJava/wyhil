package com.hnix.sd.spring.config;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hnix.sd.core.constant.ComConstants;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.MemberUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor

public class CommonInterceptor implements HandlerInterceptor {
	private final MemberUtil memberUtil;





	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		log.debug("## Request URL" + request.getRequestURI());
		
		if (!HttpMethod.OPTIONS.toString().equals(request.getMethod() )) {
			log.debug("================ preHandle ================");
			log.debug("request.getRequestURI() : {}", request.getRequestURI());
			log.debug("X-Auth-Token : {}", request.getHeader(ComConstants.HEADER));
			//log.debug("request.getSession().getId() : {}", currentRequestAttributes.getRequest().getSession(false).getId());
			log.debug("memberUtil.getUserId() : {}", memberUtil.getUserId());
	
			if (!StringUtils.hasLength( memberUtil.getUserId())) {
				throw new BizException("AccessDenied");
			}
		} else {
			this.setCors(response);
		}

		return true;
	}


	
	private void setCors(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods","*");
		response.setHeader("Access-Control-Max-Age", "60");
//		response.setHeader("Access-Control-Allow-Headers"
//				, "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token, Access-Control-Allow-Origin Content-Type, X-APIVERSION, X-APP, X-Auth-Token, X-CALLTYPE, X-CHANNEL, X-LANG, X-LOGKEY, X-MID, X-VNAME, Access-Control-Allow-Headers"
//		);
		response.setHeader("Access-Control-Allow-Headers"
				, "*"
		);
		response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
		response.setHeader("Access-Control-Expose-Headers","Authorization, X-Auth-Token, Access-Control-Allow-Origin, X-APP, X-APIVERSION");


	}
	
}
