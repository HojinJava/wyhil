package com.hnix.sd.common.mail.dto;

import java.io.File;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(title = "메일 첨부 파일 정보")
public class MailAttachDto {

	@Schema(title = "첨부파일명", example = "")
	private String attachFileName;
	@Schema(title = "첨부파일 File 객체", example = "")
	private File file;
}
