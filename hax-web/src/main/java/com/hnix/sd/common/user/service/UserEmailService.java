package com.hnix.sd.common.user.service;

import com.hnix.sd.common.user.dto.UserEmailDto;
import com.hnix.sd.common.mail.dao.MailDao;
import com.hnix.sd.common.mail.dto.MailDto;
import com.hnix.sd.common.mail.dto.MailMessageDto;
import com.hnix.sd.common.mail.service.MailService;
import com.hnix.sd.common.mail.service.MailUtils;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dao.UserEmailDao;
import com.hnix.sd.common.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserEmailService {
    private final UserEmailDao userEmailDao;
    private final UserDao userDao;
    private final UserLogService userLogService;
    private final MailService mailService;
    private final MailDao mailDao;

    @Value("${mail.survey.redirect-url}")
    private String redirectUrl;

    public boolean registerUserInfo(int fromIdx, int toIdx) {
        // UserEmail 조회
        List<UserEmailDto> userEmails = userEmailDao.findByIdxRangeAndSendYnN(fromIdx, toIdx);

        if (userEmails.isEmpty()) {
            log.warn("UserEmail 정보를 찾을 수 없습니다. fromIdx: {}, toIdx: {}", fromIdx, toIdx);
            return false;
        }

        for (UserEmailDto userEmail : userEmails) {

            String userEmailAddress = userEmail.getUserEmail();
            if (StringUtils.isEmpty(userEmailAddress)) {
                continue;
            }

            UserDto user = userDao.findByUserEmail(userEmailAddress)
                .orElseThrow(() ->
                    new RuntimeException("사용자를 찾을 수 없습니다: " + userEmailAddress));

            String userId = user.getUserId();

            // 메일 발송 대상 설정
            List<String> sendTo = new ArrayList<>();
            sendTo.add(userEmailAddress);

            // 메일 메시지 DTO 생성
            MailMessageDto mailMessageDto = new MailMessageDto();
            mailMessageDto.setHtml(true);
            mailMessageDto.setTo(sendTo);

            // 메일 템플릿 조회
            MailDto mailTemplate = mailDao.findByCompCdAndMailTypeCd("MA", "MAIL_TYPE_CONSENT_RE")
                .orElseThrow(() -> new RuntimeException("메일 템플릿을 찾을 수 없습니다.(MAIL_TYPE_CONSENT_RE)"));

            String mailCont = mailTemplate.getMailCont();
            String mailTitle = mailTemplate.getMailTitle();
            mailMessageDto.setSubject(mailTitle);

            // 개인정보 재동의 전용 링크 생성 (type=reconsent&userId=xxx)
            String getParams = String.format("userId=%s", userId);
            String reconsentLink = MailUtils.generateRedirectLink(redirectUrl, getParams);
            reconsentLink = reconsentLink + "&type=reconsent";

            // 메일 본문에 링크 삽입
            String contents = String.format(mailCont, reconsentLink != null ? reconsentLink : "");
            mailMessageDto.setContents(contents);

            // 사용자 로그 저장
            userLogService.saveUserDetailLog(
                UserLogService.ACCESS_TYPE_MAILLINK,
                userEmailAddress,
                "userId,userEmail"
            );

            // 메일 발송
            boolean sendResult = mailService.sendMail(mailMessageDto);

            if (sendResult) {
                userEmail.setSendYn("Y");
                userEmail.setSendDt(LocalDateTime.now());
                userEmailDao.save(userEmail);
            }
            
            log.info("개인정보 재동의 메일 발송 - userId: {}, email: {}, 결과: {}", 
                userId, userEmailAddress, sendResult);
          }
        return true;
      }
}
