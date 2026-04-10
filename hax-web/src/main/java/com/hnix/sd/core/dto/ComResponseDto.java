package com.hnix.sd.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class ComResponseDto<T> {
	private ResultDescDto result = null;
	private T data = null;
}
