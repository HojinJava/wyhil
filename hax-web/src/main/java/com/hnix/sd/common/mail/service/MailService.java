package com.hnix.sd.common.mail.service;

import java.util.List;

import com.hnix.sd.common.mail.dto.MailAttachDto;
import com.hnix.sd.common.mail.dto.MailMessageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hnix.sd.core.exception.BizException;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailSendUser;
	
    /**
     * 메일 발송 서비스
     * @param <T> : MultipartFile or MailAttachDto 사용
     * @param mailMessage : 메일 메세지
     * @param files : 첨부파일 리스트 (MultipartFile or MailAttachDto 사용)
     * @return
     */
    public <T> boolean sendMail(MailMessageDto mailMessage, List<T> files) {
		boolean bRet = true;

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
        	MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            if (mailMessage.getFrom() != null && !"".equals(mailMessage.getFrom())) {
            	mimeMessageHelper.setFrom(mailMessage.getFrom());
            }

            mimeMessageHelper.setFrom(mailSendUser);
            mimeMessageHelper.setTo(mailMessage.getTo().toArray(new String[mailMessage.getTo().size()])); // 메일 수신자

            mimeMessageHelper.setSubject(mailMessage.getSubject()); // 메일 제목

            if (mailMessage.getCc() != null && mailMessage.getCc().size() > 0) {
                mimeMessageHelper.setCc( mailMessage.getCc().toArray(new String[mailMessage.getCc().size()]) );
            }

            if (mailMessage.getBcc() != null && mailMessage.getBcc().size() > 0) {
              mimeMessageHelper.setBcc( mailMessage.getBcc().toArray(  mailMessage.getCc().toArray(new String[mailMessage.getBcc().size()])  ));
            }
            
            if (mailMessage.isHtml()) {
            	mimeMessageHelper.setText(mailMessage.getContents(), true);
            } else {
            	mimeMessageHelper.setText(mailMessage.getContents());
            }
            
            //첨부파일 처리
            int fileSize = 0;
            
            if (files != null ) fileSize = files.size();
            if (files != null && fileSize > 0) {
            	if( files.get(0) instanceof MultipartFile ) {
            		for(int nLoop = 0 ; nLoop < fileSize ; nLoop ++ ) {
            			MultipartFile oTemp = (MultipartFile)files.get(nLoop);
            			mimeMessageHelper.addAttachment(oTemp.getOriginalFilename() , new InputStreamResource(oTemp.getInputStream()), oTemp.getContentType());
            		}
            	} else if (files.get(0) instanceof MailAttachDto) {
            		for(int nLoop = 0 ; nLoop < fileSize ; nLoop ++ ) {
            			MailAttachDto oTemp = (MailAttachDto)files.get(nLoop);
            			mimeMessageHelper.addAttachment(oTemp.getAttachFileName(), oTemp.getFile());
            		}
            	}
            }

            javaMailSender.send(mimeMessage);

            log.info("Success");

		}
		catch (Exception e) {
            log.info("fail" , e);
            bRet = false;
            throw new BizException("UserP1_Exception", e.getMessage());
        }
		return bRet;
	}
	
	public boolean sendMail(MailMessageDto mailMessage) {
		return this.sendMail(mailMessage, null);
	}

}
