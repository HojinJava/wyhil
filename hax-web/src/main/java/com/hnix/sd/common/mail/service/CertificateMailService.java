package com.hnix.sd.common.mail.service;

import com.hnix.sd.common.mail.dao.MailDao;
import com.hnix.sd.common.mail.dto.CertificateMailDto;
import com.hnix.sd.common.mail.dto.CertificateMultiMailDto;
import com.hnix.sd.common.mail.dto.MailDto;
import com.hnix.sd.common.mail.dto.MailMessageDto;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.service.UserLogService;
import com.hnix.sd.work.software.partner.dto.PartnerDto;
import com.hnix.sd.work.software.partner.dao.PartnerDao;
import com.hnix.sd.work.software.contract.dao.ContractDao;
import com.hnix.sd.work.software.contract.dto.ContractDto;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CertificateMailService {

    private final MailService mailService;
    private final MailDao mailDao;
    private final ContractDao contractDao;
    private final UserDao userDao;
    private final UserLogService userLogService;
    private final PartnerDao partnerDao;

    @Value("${mail.survey.redirect-url}")
    private String redirectUrl;

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

    private PartnerDto getPartnerInfoBySubCode(String subCode) {
        if (StringUtils.isEmpty(subCode)) {
            throw new RuntimeException("소프트웨어 정보(subCode)가 없습니다.");
        }
        
        PartnerDto partner = partnerDao.findBySubCode(subCode);
        if (partner == null) {
            throw new RuntimeException("협력사 정보를 찾을 수 없습니다: " + subCode);
        }
        
        return partner;
    }

    private MailDto getMailTemplate(String subCode, String contractNo, String userEmail, String mailTypeCd) {
        PartnerDto partner = getPartnerInfoBySubCode(subCode);
        
        String partnerContractCd = partner.getPartnerContractCd();

        MailDto mailTemplate = null;
        
        // 직계약인 경우 고객사의 회사 템플릿 사용
        if ("COMP_CONTRACT_DIRECT".equals(partnerContractCd)) {
            ContractDto contract = contractDao.findById(contractNo).orElseThrow(() -> new RuntimeException("계약 정보를 찾을 수 없습니다: " + contractNo));
            
            if (contract != null && contract.getDeptCd() != null) {
                mailTemplate = mailDao.findByUserDeptCdAndMailTypeCd(contract.getDeptCd(), mailTypeCd)
                    .orElse(null);
            }
        }
        // 직계약이 아니거나 템플릿을 찾지 못한 경우 MA 공통 템플릿 사용
        if (mailTemplate == null) {
            mailTemplate = mailDao.findByCompCdAndMailTypeCd("MA", mailTypeCd)
                .orElseThrow(() -> new RuntimeException("메일 템플릿을 찾을 수 없습니다.(" + mailTypeCd + ")"));
        }
        
        return mailTemplate;
    }

    public void sendApprovalMailToMultiRequester(CertificateMultiMailDto certificateMultiMailDto) {
        String subCode = certificateMultiMailDto.getSubCode();
        String contractNo = certificateMultiMailDto.getContractNo();

        for (var send : certificateMultiMailDto.getSendTo()) {
            String userId = send.getUserId();
            // userId를 이메일로 변환
            String userEmail = getUserEmail(userId);

            List<String> sendTo = new ArrayList<>();
            sendTo.add(userEmail);

            MailMessageDto mailDto = new MailMessageDto();
            mailDto.setHtml(true);
            mailDto.setTo(sendTo);

            List<String> toCC = new ArrayList<>();
            if (!StringUtils.isEmpty(certificateMultiMailDto.getToCc())) {
                toCC.add(certificateMultiMailDto.getToCc());
            }
            mailDto.setCc(toCC);

            String getParams = String.format("serviceNo=%s&userId=%s", send.getServiceNo(), userId);
            String surveyLink = MailUtils.generateRedirectLink(redirectUrl, getParams);

            MailDto mailTemplate = getMailTemplate(subCode, contractNo, userEmail, "MAIL_TYPE_SURVEY");

            String mailCont = mailTemplate.getMailCont();
            String mailTitle = mailTemplate.getMailTitle();
            String contents = String.format(mailCont, surveyLink != null ? surveyLink : "");
            mailDto.setContents(contents);

            mailDto.setSubject(mailTitle);
            userLogService.saveUserDetailLog(
            UserLogService.ACCESS_TYPE_MAILLINK,
            userEmail,
            "userNm,userEmail"
        );

            mailService.sendMail(mailDto);
        }
    }

    public boolean sendApprovalMailToManager(CertificateMailDto certificateMailDto) {
        String userId = certificateMailDto.getSendTo();
        String subCode = certificateMailDto.getSubCode();
        String contractNo = certificateMailDto.getContractNo();

        // userId를 이메일로 변환
        String userEmail = getUserEmail(userId);

        List<String> sendTo = new ArrayList<>();
        sendTo.add(userEmail);

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setHtml(true);
        mailMessageDto.setTo(sendTo);

        List<String> toCC = new ArrayList<>();
        if (!StringUtils.isEmpty(certificateMailDto.getToCc())) {
            toCC.add(certificateMailDto.getToCc());
        }

        String getParams = String.format("serviceNo=%s&userId=%s", certificateMailDto.getServiceNo(), userId);
        String surveyLink = MailUtils.generateRedirectLink(redirectUrl, getParams);

        MailDto mailTemplate = getMailTemplate(subCode, contractNo, userEmail, "MAIL_TYPE_SURVEY");
        
        String mailCont = mailTemplate.getMailCont();
        String mailTitle = mailTemplate.getMailTitle();

        String contents = String.format(mailCont, surveyLink != null ? surveyLink : "");
        
        mailMessageDto.setSubject(mailTitle);

        mailMessageDto.setContents(contents);

        return mailService.sendMail(mailMessageDto);
    }

}