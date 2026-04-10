package com.hnix.sd.common.mail.service;

import com.hnix.sd.common.mail.dto.MailMessageDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailSenderTest {

    private final JavaMailSender javaMailSender;

    public String sendMail(MailMessageDto mailMessageDto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(mailMessageDto.getTo().stream().toArray(String[]::new));
            helper.setCc(mailMessageDto.getCc().stream().toArray(String[]::new));
            helper.setSubject(mailMessageDto.getSubject());
            helper.setText(mailMessageDto.getContents());
        } catch (MessagingException e) {
            return "Error while sending mail...";
        }

        javaMailSender.send(message);

        return "Mail sent success!";
    }
}
