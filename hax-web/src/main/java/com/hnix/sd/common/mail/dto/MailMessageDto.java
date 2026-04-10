package com.hnix.sd.common.mail.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailMessageDto {
	@Schema(title = "받는사람", example = "")   
	private List<String> to;
	@Schema(title = "참조", example = "")
	private List<String> cc;
	@Schema(title = "숨은참조", example = "")
	private List<String> bcc;
	@Schema(title = "제목", example = "")
	private String subject;
	@Schema(title = "내용", example = "")
	private String contents;
	@Schema(title = "본문 HTML 여부", example = "")
	private boolean html = false;
	@Hidden
	private String from;
}
