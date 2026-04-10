package com.hnix.sd.core.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ComMessageUtil {

	@Autowired
	private  MessageSource messageSource;

	
	public String getMessage(String code) {
		return getMessage(code, null);
	}

	
	public  String getMessage(String code, Object[] args) {
		String ret = "";
		
			try {
				ret = messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
			} catch (Exception e) {
				// TODO Auto-generated catch block
		
			}	
		
		return ret; 
	}
}
