package com.hnix.sd.work.registration.certificate;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.registration.certificate.service.CertificateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Certificate Controller", description = "승인여부 조회 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/work/certificate")
@RestController
public class CertificateController {

    private final CertificateService certificateService;
    private final ComResponseUtil comResponseUtil;

    @Operation(summary = "사용자 메일 인증 링크")
    @GetMapping("/client/finder")
    public ComResponseDto<?> getConfirmUserEmail(@RequestParam(value = "link") String certLink, String modId) {
        return comResponseUtil.setResponse200ok( certificateService.consentToPersonalInformation(certLink, modId));
    }

    @Operation(summary = "승인여부 데이터 조회")
    @GetMapping("/{serviceNo}")
    public ComResponseDto<?> getCertificateFromServiceNo(@PathVariable("serviceNo") String serviceNo) throws NotFoundException {
        return comResponseUtil.setResponse200ok( certificateService.getCertificateByServiceNo(serviceNo) );
    }

}
