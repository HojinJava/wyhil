package com.hnix.sd.spring.exception;

import java.text.MessageFormat;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hnix.sd.core.dto.ResultDescDto;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.ComMessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

	private final ComMessageUtil comMessageUtil;
	
	@ExceptionHandler(Exception.class)
	public @ResponseBody
	ResultDescDto handleException(Exception ex, HttpServletRequest req, HttpServletResponse response) throws Exception {

		ResultDescDto result = new ResultDescDto();
		Throwable throwable = null;
		
		String status = "";
		String desc = "";

		result.setException(true);
		result.setStatus("500");
		result.setDesc("");
		
		
		if (ex instanceof BizException) {
			BizException bizException = (BizException) ex;
			String msgKey = bizException.getMsgKey();
			log.debug("msgKey = {} /n", msgKey);
			
			if(msgKey != null) {
				status = comMessageUtil.getMessage(msgKey + ".status");
			    if(!"".equals(status)) result.setStatus(status);
                desc = 	comMessageUtil.getMessage(msgKey + ".desc");
                if(bizException.getArrayReplace() != null) {
                	desc = MessageFormat.format(desc, bizException.getArrayReplace().toArray());
                }
                result.setDesc(desc);
                result.setStatus(status);
			}
			throwable = bizException.getThrowable();
		}
		else  {
			String msgKey = "UserP1_Exception";
			log.debug("msgKey = {} /n", msgKey);
			
			status = comMessageUtil.getMessage(msgKey + ".status");
		    if(!"".equals(status)) result.setStatus(status);
            desc = 	comMessageUtil.getMessage(msgKey + ".desc");
            
            desc = MessageFormat.format(desc, ex.getMessage());
            
            result.setDesc(desc);
            result.setStatus(status);
			
		}

		log.info("# Exception.getMessage() : {}", ex.getMessage());

		response.setStatus(Integer.parseInt(result.getStatus()));

		return result;
	}
}
