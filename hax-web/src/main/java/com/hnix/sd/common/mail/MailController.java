package com.hnix.sd.common.mail;

import java.util.List;

import com.hnix.sd.common.mail.service.MailSenderTest;
import com.hnix.sd.common.mail.service.MailService;
import com.hnix.sd.common.mail.dto.MailMessageDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Tag(name = "Mail Controller", description = "怨듯넻 硫붿씪 諛쒖넚 紐⑤뱢") 
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/mail")
public class MailController {

	private final MailService mailService;
	private final MailSenderTest mailSenderTester;

	private final ComResponseUtil comResponseUtil;

	@PostMapping("/test/send")
	public ComResponseDto<?> sendTest(@RequestBody MailMessageDto mailMessageDto) {
		return comResponseUtil.setResponse200ok(mailSenderTester.sendMail(mailMessageDto));
	}

	@Operation(summary = "硫붿씪 諛쒖넚 - 泥⑤? ?놁쓬")
	@PostMapping("/send")
	public ComResponseDto<Object> sendMail(@RequestBody MailMessageDto mailMessageDto) {
		return comResponseUtil.setResponse200ok(mailService.sendMail(mailMessageDto));
	}

	@Operation(summary = "硫붿씪 諛쒖넚 - 泥⑤? ?ы븿")
	@PostMapping("/sendwithattach")
	public ComResponseDto<Object> sendMailWithAttachFile(@Parameter (description="硫붿씪 ?댁슜" ) @RequestBody MailMessageDto mailMessageDto ,@Parameter (description="MultipartFile List" ) @RequestParam List<MultipartFile> file) {
		return comResponseUtil.setResponse200ok(mailService.sendMail(mailMessageDto , file));
	}
}
