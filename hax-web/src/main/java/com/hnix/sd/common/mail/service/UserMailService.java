package com.hnix.sd.common.mail.service;

import com.hnix.sd.common.mail.dao.MailDao;
import com.hnix.sd.common.mail.dto.*;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.service.UserLogService;
import com.hnix.sd.core.exception.BizException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hnix.sd.core.utils.SecurtyUtil.sha512;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserMailService {

    private final UserDao userDao;
    private final MailService mailService;
    private final UserLogService userLogService;
    private final MailDao mailDao;

    @Value("${mail.certificate.redirect-url}")
    private String certificateUrl;

    @Value("${mail.login-url}")
    private String loginUrl;

    private char[] charSet = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '!', '@', '#', '$', '%', '^', '&'
    };

    // 이메일 변환
    private String getUserEmail(String userIdOrEmail) {
        if (StringUtils.isEmpty(userIdOrEmail)) {
            throw new RuntimeException("사용자 정보가 없습니다.");
        }
        
        // 이미 이메일 형식이면 그대로 반환
        if (userIdOrEmail.contains("@")) {
            return userIdOrEmail;
        }
        
        // userId 형식이면 이메일 조회
        UserDto user = userDao.findById(userIdOrEmail)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIdOrEmail));
        
        if (StringUtils.isEmpty(user.getUserEmail())) {
            throw new RuntimeException("사용자의 이메일 정보가 없습니다: " + userIdOrEmail);
        }
        
        return user.getUserEmail();
    }

    //메일 발송
    public boolean resetUserPassword(ResetPwMailDto mailDto) {

        String userEmail = getUserEmail(mailDto.getTo());

        List<String> sendTo = new ArrayList<>();
        sendTo.add(userEmail);

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setHtml(true);
        mailMessageDto.setTo( sendTo );

        List<String> toCC = new ArrayList<>();
//        toCC.add("willy0408@hnix.co.kr");

        if (!StringUtils.isEmpty(mailDto.getCc())) {
            toCC.add(mailDto.getCc());
        }

        mailMessageDto.setCc( toCC );

        String newPassword = getRandomPassword(10);

        updatedUserPassword(userEmail, newPassword);

        // 수신자의 고객사 코드 조회 - v_organization_structure 사용
        UserDto user = userDao.findByUserEmail(userEmail).orElse(null);
        
        MailDto mailTemplate = null;
        if (user != null && user.getDeptCd() != null) {
            // 사용자 부서의 상위 회사코드로 조회
            mailTemplate = mailDao.findByUserDeptCdAndMailTypeCd(user.getDeptCd(), "MAIL_TYPE_PW")
                .orElse(null);
        }
        
        // 고객사 템플릿이 없으면 MA 기본 템플릿 사용
        if (mailTemplate == null) {
            mailTemplate = mailDao.findByCompCdAndMailTypeCd("MA", "MAIL_TYPE_PW")
                .orElseThrow(() -> new RuntimeException("메일 템플릿을 찾을 수 없습니다.(MAIL_TYPE_PW)"));
        }
        String mailCont = mailTemplate.getMailCont();
        String mailTitle = mailTemplate.getMailTitle();
        
        mailMessageDto.setSubject( mailTitle );
        String contents = String.format(mailCont, newPassword != null ? newPassword : "");
        mailMessageDto.setContents( contents );

        if(!mailService.sendMail( mailMessageDto )) {
            //메일 발송 실패
            return false;
        }

        if (user != null && !StringUtils.isEmpty(user.getUserId())) {
            user.setUserPwResetYn('Y');
            userDao.save(user);
        }

        userLogService.saveUserDetailLog(
            UserLogService.ACCESS_TYPE_MAILLINK,
            userEmail,  // 조회 대상 사용자
            "userNm,userEmail"  // 노출되는 필드들
        ); 
        
        return true;
    }









    public boolean confirmUserRegister(ConfirmUserInfoDto mailDto) {
        String userEmail = getUserEmail(mailDto.getTo());

        List<String> sendTo = new ArrayList<>();
        sendTo.add(userEmail);

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setHtml(true);
        mailMessageDto.setTo( sendTo );

        List<String> toCC = new ArrayList<>();

        if (!StringUtils.isEmpty(mailDto.getCc())) {
            toCC.add(mailDto.getCc());
        }

        mailMessageDto.setCc( toCC );

        final String newPassword = getRandomPassword(10);

        updatedUserPassword(userEmail, newPassword);

        // 수신자의 고객사 코드 조회 - v_organization_structure 사용
        UserDto user = userDao.findByUserEmail(userEmail).orElse(null);
        
        MailDto mailTemplate = null;
        if (user != null && user.getDeptCd() != null) {
            mailTemplate = mailDao.findByUserDeptCdAndMailTypeCd(user.getDeptCd(), "MAIL_TYPE_ADD_USER")
                .orElse(null);
        }
        
        if (mailTemplate == null) {
            mailTemplate = mailDao.findByCompCdAndMailTypeCd("MA", "MAIL_TYPE_ADD_USER")
                .orElseThrow(() -> new RuntimeException("메일 템플릿을 찾을 수 없습니다.(MAIL_TYPE_ADD_USER)"));
        }
        String mailCont = mailTemplate.getMailCont();
        String mailTitle = mailTemplate.getMailTitle();
        
        mailMessageDto.setSubject( mailTitle );
        String contents = String.format(
            mailCont,
            loginUrl != null ? loginUrl : "",                    // 첫 번째 %s - [HAX-WEB] 링크
            mailDto.getCompany() != null ? mailDto.getCompany() : "",  // 두 번째 %s - 소속
            mailDto.getUserNm() != null ? mailDto.getUserNm() : "",    // 세 번째 %s - 성명
            userEmail,      // 네 번째 %s - 이메일
            newPassword != null ? newPassword : "",                    // 다섯 번째 %s - 초기 비밀번호
            loginUrl != null ? loginUrl : ""                           // 여섯 번째 %s - [HAX-WEB] 링크 (하단)
        );
        mailMessageDto.setContents( contents );

        userLogService.saveUserDetailLog(
            UserLogService.ACCESS_TYPE_MAILLINK,
            userEmail,  // 조회 대상 사용자
            "userNm,userEmail"  // 노출되는 필드들
        ); 
        return mailService.sendMail( mailMessageDto );
    }


    public boolean updatedConfirmUserInfo(UpdateUserInfoDto mailDto) {
        String userEmail = getUserEmail(mailDto.getTo());

        List<String> sendTo = new ArrayList<>();
        sendTo.add(userEmail);

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setHtml(true);
        mailMessageDto.setTo( sendTo );

        List<String> toCC = new ArrayList<>();

        if (!StringUtils.isEmpty(mailDto.getCc())) {
            toCC.add(mailDto.getCc());
        }

        mailMessageDto.setCc( toCC );

        String userName = mailDto.getUserNm();

        // 수신자의 고객사 코드 조회 - v_organization_structure 사용
        UserDto user = userDao.findByUserEmail(userEmail).orElse(null);
        
        MailDto mailTemplate = null;
        if (user != null && user.getDeptCd() != null) {
            log.info("사용자 deptCd: {}", user.getDeptCd());
            mailTemplate = mailDao.findByUserDeptCdAndMailTypeCd(user.getDeptCd(), "MAIL_TYPE_UPDATE_USER")
                .orElse(null);
                log.info("조회된 mailTemplate compCd: {}", 
           mailTemplate != null ? mailTemplate.getCompCd() : "null");
        }
        
        if (mailTemplate == null) {
            mailTemplate = mailDao.findByCompCdAndMailTypeCd("MA", "MAIL_TYPE_UPDATE_USER")
                .orElseThrow(() -> new RuntimeException("메일 템플릿을 찾을 수 없습니다.(MAIL_TYPE_UPDATE_USER)"));
        }
        String mailCont = mailTemplate.getMailCont();
        String mailTitle = mailTemplate.getMailTitle();
        
        mailMessageDto.setSubject( mailTitle );

        String contents = String.format(mailCont,
        StringUtils.isEmpty(userName) ? "" : userName + "님 ",
        loginUrl,
        String.format("%s / %s", 
            mailDto.getCompany() != null ? mailDto.getCompany() : "",
            mailDto.getUserDeptNm() != null ? mailDto.getUserDeptNm() : ""),
        mailDto.getUserPosition() != null ? mailDto.getUserPosition() : "",
        mailDto.getUserPhone() != null ? mailDto.getUserPhone() : "",
        mailDto.getUserPhoneOffice() != null ? mailDto.getUserPhoneOffice() : "");

        mailMessageDto.setContents( contents );

        userLogService.saveUserDetailLog(
            UserLogService.ACCESS_TYPE_MAILLINK,
            userEmail,  // 조회 대상 사용자
            "userNm,userEmail,userPhoneMobile"  // 노출되는 필드들
        ); 

        return mailService.sendMail( mailMessageDto );
    }


    public boolean certificateUserEmail(CheckUserMailDto mailDto) {
        List<String> sendTo = new ArrayList<>();
        sendTo.add( mailDto.getUserEmail() );

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setHtml(true);
        mailMessageDto.setTo( sendTo );

        List<String> toCC = new ArrayList<>();

        if (!StringUtils.isEmpty(mailDto.getCc())) {
            toCC.add(mailDto.getCc());
        }

        mailMessageDto.setCc( toCC );

        String getParams = String.format("userEmail=%s", mailDto.getUserEmail());
        String certificateRedirectUrl = MailUtils.generateRedirectLink( certificateUrl, getParams );

        // 수신자의 고객사 코드 조회
        UserDto user = userDao.findByUserEmail(mailDto.getUserEmail()).orElse(null);
        String compCd = (user != null && user.getDeptCd() != null) ? user.getDeptCd() : "MA";

        // DB에서 메일 템플릿 조회 (고객사 양식 → MA 양식)
        MailDto mailTemplate = mailDao.findByCompCdAndMailTypeCd(compCd, "MAIL_TYPE_CERT")
            .orElse(mailDao.findByCompCdAndMailTypeCd("MA", "MAIL_TYPE_CERT").orElse(null));
        if (mailTemplate == null) {
            throw new RuntimeException("메일 템플릿을 찾을 수 없습니다.(MAIL_TYPE_CERT)");
        }

        String mailCont = mailTemplate.getMailCont();
        String mailTitle = mailTemplate.getMailTitle();
        
        mailMessageDto.setSubject( mailTitle );
        String contents = String.format(mailCont, certificateRedirectUrl);
        
        mailMessageDto.setContents( contents );

        userLogService.saveUserDetailLog(
        UserLogService.ACCESS_TYPE_MAILLINK,
        mailDto.getUserEmail(),
        "userNm,userEmail"
        ); 

        return mailService.sendMail( mailMessageDto );
    }


    @Transactional
    public boolean updatedUserPassword(String userEmail, String newPassword) {
        UserDto user = userDao.findByUserEmail(userEmail).orElseGet(UserDto::new);

        if (!StringUtils.isEmpty(user.getUserId())) {
            user.setUserPw(sha512(newPassword));
            userDao.save(user);
            return true;
        }
        return false;
    }

    private String getRandomPassword(int size) {
        StringBuffer buffer = new StringBuffer();
        SecureRandom random = new SecureRandom();
        random.setSeed(new Date().getTime());

        final int len = charSet.length - 1;

        for (int i = 0; i < size; i++) {
            char randomCharacter = charSet[random.nextInt(len)];
            buffer.append(randomCharacter);
        }
        return buffer.toString();
    }

    private String checkSearchParameter(String str) {
        return StringUtils.isEmpty( str ) ? "" : str;
    }


    // 메일 목록 조회
    public List<MailDto> searchMailWithCompCd(MailDto mailDto) {
        String compCd = (mailDto.getCompCd() == null) ? "" : mailDto.getCompCd();
    
        return mailDao.findByCompCd(compCd)
                .stream()
                .map(row -> {
                    MailDto dto = new MailDto();
                    dto.setDeptNm((String) row[0]);           // 회사명
                    dto.setCodeText((String) row[1]);         // 메일종류명
                    dto.setCompCd((String) row[2]);           // 고객사코드
                    dto.setMailTypeCd((String) row[3]);       // 메일종류코드
                    dto.setMailTitle((String) row[4]);         // 메일종류명
                    dto.setMailCont((String) row[5]);         // 메일내용
                    dto.setMailDesc((String) row[6]);         // 설명
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 메일 목록 수정 및 추가
    public void updateMailList(MailDto mailDto) {
        MailDto mail = mailDao.findByCompCdAndMailTypeCd(
                mailDto.getCompCd(), 
                mailDto.getMailTypeCd()
        ).orElseGet(MailDto::new);
        
        if (StringUtils.isEmpty(mail.getRegId())) {
            mail.setRegId(mailDto.getRegId());
            mail.setCompCd(mailDto.getCompCd());
            mail.setMailTypeCd(mailDto.getMailTypeCd());
        } 
        else {
            mail.setModId(mailDto.getModId());
            mail.setModDt(LocalDateTime.now());
        }
        
        mail.setMailTitle(mailDto.getMailTitle());
        mail.setMailCont(mailDto.getMailCont());
        mail.setMailDesc(mailDto.getMailDesc());
        
        mailDao.save(mail);
    }

    // 메일 목록 삭제
    public void deleteMailList(MailDto mailDto) {
        MailDto mail = mailDao.findByCompCdAndMailTypeCd(
            mailDto.getCompCd(), 
            mailDto.getMailTypeCd()
        ).orElseThrow(() -> new BizException("삭제할 메일 정보가 없습니다."));
        
        mailDao.delete(mail);
    }
}
