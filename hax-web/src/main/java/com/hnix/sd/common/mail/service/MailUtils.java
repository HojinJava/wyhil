package com.hnix.sd.common.mail.service;

import com.hnix.sd.core.utils.EncryptionUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MailUtils {

    public static String generateRedirectLink(String redirectUrl, String getParams) {
        String encryptionServiceNo = EncryptionUtils.encrypt( getParams );
        return String.format("%s?params=%s", redirectUrl, encryptionServiceNo);
    }

    public static String getMailTemplate(String templateHtmlName) {
        final String resourceLocation = String.format("template/%s", templateHtmlName);

        try {
            ClassPathResource resource = new ClassPathResource(resourceLocation);
            byte[] fileBytes = resource.getInputStream().readAllBytes();
            return new String(fileBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

}
