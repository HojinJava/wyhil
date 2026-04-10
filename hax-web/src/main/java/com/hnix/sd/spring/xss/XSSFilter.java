package com.hnix.sd.spring.xss;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XSSFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		log.info("=============================== XSSFilter Start =================================  ");
		log.info("Request Method =>{}" , httpServletRequest.getMethod() );
		log.info("Request URI =>{}" , httpServletRequest.getRequestURL() );
		log.info("=============================== XSSFilter End   =================================  ");
		chain.doFilter(new XSSRequestWrapper(httpServletRequest), response);
	}

}
