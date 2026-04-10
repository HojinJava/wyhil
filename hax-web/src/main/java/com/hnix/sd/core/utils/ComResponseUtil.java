package com.hnix.sd.core.utils;

import org.springframework.stereotype.Component;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.dto.ResultDescDto;
import com.hnix.sd.core.exception.BizException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ComResponseUtil {
	private final ComMessageUtil comMessageUtil;
	public ComResponseDto<?> setResponse200ok() {
		ComResponseDto<Object> comResponseDto = new ComResponseDto<Object>();
		ResultDescDto resultDto = new ResultDescDto();
		
		resultDto.setStatus(comMessageUtil.getMessage("200ok.status"));
		resultDto.setDesc(comMessageUtil.getMessage("200ok.desc"));
		
		comResponseDto.setResult(resultDto);
		return comResponseDto;
	}
	
	public <T> ComResponseDto<T> setResponse200ok(T parameter) {
		ComResponseDto<T> comResponseDto = new ComResponseDto<T>();
		ResultDescDto resultDto = new ResultDescDto();
		
		resultDto.setStatus(comMessageUtil.getMessage("200ok.status"));
		resultDto.setDesc(comMessageUtil.getMessage("200ok.desc"));
		comResponseDto.setResult(resultDto);
		comResponseDto.setData(parameter);
		return comResponseDto;
	}
	public ComResponseDto<?> setResponse(String msgKey) {
		ComResponseDto<Object> comResponseDto = new ComResponseDto<Object>();
		ResultDescDto resultDto = new ResultDescDto();
		
		String status = comMessageUtil.getMessage(msgKey + ".status");
		
		if(!"".equals(status) && "200".equals(status)) {
			resultDto.setStatus(comMessageUtil.getMessage(msgKey + ".status"));
			resultDto.setDesc(comMessageUtil.getMessage(msgKey + ".desc"));
			
		} else {
			throw new BizException(msgKey);
		}
		
		
		comResponseDto.setResult(resultDto);
		return comResponseDto;
	}
}
