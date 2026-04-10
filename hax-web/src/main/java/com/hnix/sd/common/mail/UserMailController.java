package com.hnix.sd.common.mail;

import com.hnix.sd.common.mail.dto.CheckUserMailDto;
import com.hnix.sd.common.mail.dto.ResetPwMailDto;
import com.hnix.sd.common.mail.dto.UpdateUserInfoDto;
import com.hnix.sd.common.mail.service.UserMailService;
import com.hnix.sd.common.mail.dto.ConfirmUserInfoDto;
import com.hnix.sd.common.mail.dto.MailDto;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Mail Controller", description = "User Mail Management")
@RequiredArgsConstructor
@RequestMapping("/common/mail/user")
@RestController
public class UserMailController {

    private final UserMailService userMailService;
    private final ComResponseUtil comResponseUtil;

    @Operation(summary = "Mail Send - Reset Password")
    @PostMapping("/reset/password")
    public ComResponseDto<Object> resetUserPassword(@RequestBody ResetPwMailDto mailMessageDto) {
        return comResponseUtil.setResponse200ok(userMailService.resetUserPassword(mailMessageDto));
    }

    @Operation(summary = "메일 발송 - 사용자 정보 등록 안내")
    @PostMapping("/confirm")
    public ComResponseDto<Object> confirmUserInfo(@RequestBody ConfirmUserInfoDto mailMessageDto) {
        return comResponseUtil.setResponse200ok(userMailService.confirmUserRegister(mailMessageDto));
    }

    @Operation(summary = "Mail Send - Update User Info")
    @PostMapping("/update")
    public ComResponseDto<Object> updatedConfirmUserInfo(@RequestBody UpdateUserInfoDto mailMessageDto) {
        return comResponseUtil.setResponse200ok(userMailService.updatedConfirmUserInfo(mailMessageDto) );
    }

    @Operation(summary = "메일 발송 - 사용자 인증")
    @PostMapping("/certificate")
    public ComResponseDto<Object> certificateUserEmail(@RequestBody CheckUserMailDto mailMessageDto) {
        return comResponseUtil.setResponse200ok(userMailService.certificateUserEmail(mailMessageDto) );
    }

    @Operation(summary = "메일 목록 검색 (조건: 고객사명)")
    @PostMapping("/search")
    public ComResponseDto<?> searchMailWithCompCd(@RequestBody MailDto mailDto) {
        return comResponseUtil.setResponse200ok(userMailService.searchMailWithCompCd(mailDto));
    }

    @Operation(summary = "메일 목록 수정 및 추가")
    @PostMapping("/store")
    public ComResponseDto<?> updateMailList(@RequestBody MailDto mailDto) {
        userMailService.updateMailList(mailDto);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "메일 목록 삭제")
    @PostMapping("/delete")
    public ComResponseDto<?> deleteMailList(@RequestBody MailDto mailDto) {
        userMailService.deleteMailList(mailDto);
        return comResponseUtil.setResponse200ok();
    }
}
