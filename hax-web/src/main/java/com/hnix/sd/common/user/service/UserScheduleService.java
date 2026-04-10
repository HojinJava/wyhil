package com.hnix.sd.common.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hnix.sd.common.mail.dto.MailMessageDto;
import com.hnix.sd.common.mail.service.MailService;
import com.hnix.sd.common.mail.dao.MailDao;
import com.hnix.sd.common.mail.dto.MailDto;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.history.dao.CommonHistoryDao;
import com.hnix.sd.common.history.dto.CommonHistoryDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserScheduleService {

    private final UserDao userDao;
    private final MailDao mailDao;
    private final MailService mailService;
    private final CommonHistoryDao commonHistoryDao;

    @Value("${hnix.user.consent-temp-days}")
    private String consentTempDays;
    @Value("${hnix.user.consent-use-days}")
    private String consentUseDays;
    @Value("${hnix.user.consent-open-day:2026-01-12}")
    private String consentOpenDay;
    @Value("${mail.privacy-url}")
    private String privacyUrl;

    @Transactional
    public void updateUserConsentStatus() {
        int tempDays = Integer.parseInt(consentTempDays);
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime openDt = LocalDateTime.parse(consentOpenDay + "T00:00:00");

        // 시스템 오픈 시점부터 지정 기간 안인 경우 제외 (30일 지난 이후부터 프로세스 수행)
        if(openDt.plusDays(tempDays).isAfter(nowDateTime)) {
            return;
        }

        List<UserDto> userList = userDao.findAll();

        for(UserDto user : userList) {
            String userId = user.getUserId();
            String userEmail = user.getUserEmail();

            String logMsg = "userId = " + userId + " / ";

            // 관리자 계정이거나 이미 재동의 만료된 사용자 제외
            if(userId.equals("admin") || userId.equals("SYSTEM") || userEmail == null) {
                continue;
            }

            LocalDateTime consentDt = user.getUserConsentDt();
			Character consentYn = user.getUserConsentYn();
			LocalDateTime regDt = user.getRegDt();

            int useDays = Integer.parseInt(consentUseDays);

            // (신규 등록 사용자)동의일 비어 있고, 등록일로 부터 지정된 기간이 지난 경우
            if((consentYn == 'N' || consentDt == null) && regDt.plusDays(tempDays).isBefore(nowDateTime)) {
                // 동의 만료 처리
                int updateExpireConsent = updateUserExpireConsent(userId);

                log.info(logMsg + "Expire Consent = " + updateExpireConsent + " / (신규 등록 사용자)동의일 비어 있고, 등록일로 부터 지정된 기간이 지난 경우");
                continue;
            }

            // 동의 상태이고 동의일로 부터 5년이 지난 경우 사용자 만료
            if(consentYn == 'Y' && consentDt.plusDays(useDays).isBefore(nowDateTime)) {
                // 동의 만료 처리
                int updateExpireConsent = updateUserExpireConsent(userId);

                log.info(logMsg + "Expire Consent = " + updateExpireConsent + " / 동의 일로 부터 5년이 지난 사용자 만료");
                continue;
            }

            // 동의 상태 사용자이고, 동의 일로부터 1년이 지난 사용자에게 개인정보 이용 안내 메일 발송
            if(consentYn == 'Y' && isExactlyOneYearPassed(consentDt, nowDateTime)) {
                // 메일 발송
                boolean sendMail = sendUserConsentMail(userEmail);
                int insertHistory = 0;
                if(sendMail) {
                    insertHistory = insertCommonHistoryForConsent(userId);
                }
                log.info(logMsg + "개인정보 이용 안내 메일 전송 결과 = " + sendMail + " / 이력 등록 결과 = " + insertHistory);
                continue;
            }
        }
    }

    public boolean isExactlyOneYearPassed(LocalDateTime consentDt, LocalDateTime now) {
        int diffYear = now.getYear() - consentDt.getYear();
        if (diffYear < 1) return false;
        return consentDt.plusYears(diffYear).toLocalDate().equals(now.toLocalDate());
    }

    public int updateUserExpireConsent(String userId) {
        int isUpdate = userDao.updateUserExpireConsentByUserId(userId);
        expireCommonHistoryForConsent(userId);
        return isUpdate;
    }

    public int expireCommonHistoryForConsent(String userId) {
        CommonHistoryDto history = new CommonHistoryDto();
        history.setUserId(userId);
        history.setMenuCd("common-dept-user");
        history.setHisTypeCd("COMMON_HISTORY_TYPE_CONSENT_EX");
        history.setHisContents("");
        history.setHisDt(LocalDateTime.now());
        
        commonHistoryDao.insert(history);
        return 1;
    }

    public int insertCommonHistoryForConsent(String userId) {
        CommonHistoryDto history = new CommonHistoryDto();
        history.setUserId(userId);
        history.setMenuCd("common-dept-user");
        history.setHisTypeCd("COMMON_HISTORY_TYPE_CONSENT_US");
        history.setHisContents("");
        history.setHisDt(LocalDateTime.now());
        
        commonHistoryDao.insert(history);
        return 1;
    }

    public boolean sendUserConsentMail(String userEmail) {
        List<String> sendTo = new ArrayList<>();
        sendTo.add(userEmail);

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setHtml(true);
        mailMessageDto.setTo( sendTo );

        // DB에서 메일 템플릿 조회 (고객사 양식 → MA 양식)
        MailDto mailTemplate = mailDao.findByCompCdAndMailTypeCd("MA", "MAIL_TYPE_CONSENT_USE")
            .orElseThrow(() -> new RuntimeException("메일 템플릿을 찾을 수 없습니다.(MAIL_TYPE_CONSENT_USE)"));

        String mailCont = mailTemplate.getMailCont();
        String mailTitle = mailTemplate.getMailTitle();
        mailMessageDto.setSubject( mailTitle );

        String contents = String.format(mailCont, privacyUrl);
        mailMessageDto.setContents( contents );

        return mailService.sendMail( mailMessageDto );
    }
}
