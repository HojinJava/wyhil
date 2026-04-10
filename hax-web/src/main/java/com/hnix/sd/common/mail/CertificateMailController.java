package com.hnix.sd.common.mail;

import com.hnix.sd.common.mail.dto.CertificateMailDto;
import com.hnix.sd.common.mail.dto.CertificateMultiMailDto;
import com.hnix.sd.common.mail.service.CertificateMailService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag(name = "Certificate Controller", description = "승인 요청 메일 전송 및 설문조사 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/mail/request")
@RestController
public class CertificateMailController {

    private final CertificateMailService certificateMailService;
    private final ComResponseUtil responseUtil;

    @Operation(description = "다수 작업 요청자에게 승인 요청 메일 전송")
    @PostMapping("/multi/approve")
    public ComResponseDto<?> sendApprovalMailToMultiRequester(@RequestBody CertificateMultiMailDto mailDto) {
        certificateMailService.sendApprovalMailToMultiRequester(mailDto);
        return responseUtil.setResponse200ok(  );
    }

    //승인메일 발송
    @Operation(description = "작업 요청자에게 승인 요청 메일 전송")
    @PostMapping("/approve")
    public ComResponseDto<?> sendApprovalMailToManager(@RequestBody CertificateMailDto mailDto) {
        return responseUtil.setResponse200ok( certificateMailService.sendApprovalMailToManager(mailDto) );
    }

    @GetMapping("/test")
    public ComResponseDto<?> test() {
        String html = getHtmlTemplate("template/mail.html");
        return responseUtil.setResponse200ok(html);
    }

    private String getHtmlTemplate(String templatePath) {
        String html = getHtmlTemplate("template/mail.html");

        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            byte[] fileBytes = resource.getInputStream().readAllBytes();
            return new String(fileBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
