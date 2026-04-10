package com.hnix.sd.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResultDescDto {
	private String desc = null;
	private String status = null;
	private boolean isException = false;
}
