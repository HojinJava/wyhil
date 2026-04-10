package com.hnix.sd.common.user;

import com.hnix.sd.common.user.service.UserCertService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User Cert Controller", description = "사용자 초기 인증 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/user")
@RestController
public class UserCertController {

    private final UserCertService userService;
    private final ComResponseUtil comResponseUtil;

    @Operation(summary = "사용자 개인정보활용 동의 여부 체크")
    @GetMapping("/consent/check/{userId}")
    public ComResponseDto<?> checkUserConsent(@PathVariable("userId") String userId) {
        return comResponseUtil.setResponse200ok(userService.checkUserConsent(userId));
    }

    @Operation(summary = "사용자 개인정보활용 동의 시")
    @GetMapping("/consent/agree/{userId}")
    public ComResponseDto<?> updatedUserConsentStatus(@PathVariable("userId") String userId) {
        userService.updatedUserConsentStatus(userId);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "사용자 메일 인증 여부 체크")
    @GetMapping("/certificate/check/{userEmail}")
    public ComResponseDto<?> checkUserCertificate(@PathVariable("userEmail") String userEmail) {
        return comResponseUtil.setResponse200ok(userService.checkUserCertificate(userEmail));
    }

    @Operation(summary = "사용자 메일 인증 완료 시")
    @GetMapping("/certificate/agree/{userEmail}")
    public ComResponseDto<?> updatedUserCertificateStatus(@PathVariable("userEmail") String userEmail, @RequestParam(required = false) String modId) {
        userService.updatedUserCertificateStatus(userEmail, modId);
        return comResponseUtil.setResponse200ok();
    }



    @Operation(summary = "비밀번호 찾기 - 사용자 확인")
    @PostMapping("/certificate/user")
    public ComResponseDto<?> certUser(@RequestBody Map<String, Object> mapParam) {
        return comResponseUtil.setResponse200ok(userService.certUser(mapParam));
    }


}
