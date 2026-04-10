package com.hnix.sd.common.user.service;

import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.dto.ChangeUserInfoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCertService {

    private final UserDao userDao;
    private final UserHistoryService userHistoryService;

    public boolean checkUserConsent(String userId) {
        UserDto user = userDao.findByUserId(userId).orElseGet(UserDto::new);

        return user.getUserConsentYn() != null && user.getUserConsentYn() == 'Y';
    }

    public void updatedUserConsentStatus(String userId) {
        UserDto user = userDao.findByUserId(userId).orElseGet(UserDto::new);

        if ( !StringUtils.isEmpty(user.getUserId()) ) {
            user.setUserConsentYn('Y');
            user.setUserConsentDt(LocalDateTime.now());
            userDao.save(user);

            ChangeUserInfoDto dto = new ChangeUserInfoDto();
            dto.setUserId(userId);
            dto.setModId(userId);
            userHistoryService.consentUserHistory(user, dto);
        }
    }

    public boolean checkUserCertificate(String userId) {
        UserDto user = userDao.findByUserId(userId).orElseGet(UserDto::new);

        return user.getUserCertYn() != null && user.getUserCertYn() == 'Y';
    }

    public void updatedUserCertificateStatus(String userEmail, String modId) {
        UserDto user = userDao.findByUserEmail(userEmail).orElseGet(UserDto::new);

        if ( !StringUtils.isEmpty(user.getUserId()) ) {
            user.setUserCertYn('Y');
            user.setUserCertDt(LocalDateTime.now());
            userDao.save(user);

            ChangeUserInfoDto dto = new ChangeUserInfoDto();
            dto.setUserId(user.getUserId());
            dto.setModId(modId);
            userHistoryService.certUserHistory(user, dto);
        }
    }

    public boolean certUser(Map<String, Object> mapParam) {

        String userEmail = (String) mapParam.get("email");
        String userName = (String) mapParam.get("name");

        UserDto user = userDao.findByUserEmail(userEmail).orElseGet(UserDto::new);

        if(user.getUserNm() != null && user.getUserNm().equalsIgnoreCase(userName)) {
            return true;
        }

        return false;
    }

}
