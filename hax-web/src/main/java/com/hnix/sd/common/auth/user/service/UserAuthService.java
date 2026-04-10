package com.hnix.sd.common.auth.user.service;

import com.hnix.sd.common.auth.user.dto.AuthInfoDto;
import com.hnix.sd.common.auth.user.dto.AuthUserInfoDto;
import com.hnix.sd.common.auth.user.dto.FailedUserAuthDto;
import com.hnix.sd.common.auth.user.dto.UserAuthIdsDto;
import com.hnix.sd.common.auth.user.dto.UserAuthDto;
import com.hnix.sd.common.auth.user.dao.UserAuthDao;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserAuthService {
    private final UserAuthDao userAuthDao;

    //사용자 & 회사/사업부 권한 목록 조회
    @Transactional(readOnly = true)
    public List<AuthUserInfoDto> getUserAuthListFromGroupCd(String groupCd, String typeCd) {
        List<AuthUserInfoDto> deptInfo = userAuthDao.findByGroupCdWithDept(groupCd);
        List<AuthUserInfoDto> userInfo = userAuthDao.findByGroupCdWithUser(groupCd);
        deptInfo.addAll(userInfo);
        return deptInfo;
    }

    //사용자 & 회사/사업부 권한 저장 (insert)
    public List<FailedUserAuthDto> updatedMultipleUserAuth(List<AuthInfoDto> authInfoDtos) {
        List<UserAuthDto> userEntities = new ArrayList<>();
        List<FailedUserAuthDto> failedList = new ArrayList<>();

        for (var userAuthDto : authInfoDtos) {
            String userId = userAuthDto.getUserId();
            String groupCd = userAuthDto.getGroupCd();

            if ( !StringUtils.isEmpty(userId) ||
                    !StringUtils.isEmpty(groupCd) ) {
                userEntities.add(UserAuthDto.builder()
                        .userId(userAuthDto.getUserId())
                        .groupCd(userAuthDto.getGroupCd())
                        .userTypeCd(userAuthDto.getUserTypeCd())
                        .regId(userAuthDto.getRegId())
                        .modId(userAuthDto.getModId())
                        .build());
                continue;
            }
            failedList.add(new FailedUserAuthDto(userId, groupCd));
        }

        if(!userEntities.isEmpty()) {
            userAuthDao.insertUserAuthList(userEntities);
        }

        return failedList;
    }

    //사용자 권한 추가 (insert)
    public void storeUserDefaultAuth(String userId, String regId, String companyTypeCd) {

        UserAuthDto userAuth = new UserAuthDto();

        userAuth.setGroupCd("USER"); //기본값. 장애방지
        if(companyTypeCd.equalsIgnoreCase("C")) { userAuth.setGroupCd("USER"); }
        if(companyTypeCd.equalsIgnoreCase("P")) { userAuth.setGroupCd("SUPPORT"); }

        userAuth.setUserId(userId);
        userAuth.setUserTypeCd('U');
        
        userAuth.setRegId(regId);
        userAuth.setRegDt(LocalDateTime.now());

        userAuthDao.insertUserAuth(userAuth);
    }

    //사용자 & 회사/사업부 권한 삭제
    @Transactional
    public void removeMultipleUserAuth(List<UserAuthIdsDto> userAuthDtoList) {

        for (var ids : userAuthDtoList) {
            if ( !checkUserAuthIdsNull(ids) ) {

                userAuthDao.deleteByUserIdAndGroupCdAndUserTypeCd(
                    ids.getUserId(),
                    ids.getGroupCd(),
                    ids.getUserTypeCd()
                );
            }
        }

    }

    private boolean checkUserAuthIdsNull(UserAuthIdsDto ids) {
        return StringUtils.isEmpty(ids.getUserId()) ||
                StringUtils.isEmpty(ids.getGroupCd()) ||
                (ids.getUserTypeCd() == null || ids.getUserTypeCd() == ' ');
    }
}
