package com.hnix.sd.common.user.service;

import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.*;
import com.hnix.sd.common.department.dto.DepartmentStructureDto;
import com.hnix.sd.common.auth.user.service.UserAuthService;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.MemberUtil;
import com.hnix.sd.core.utils.SecurtyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hnix.sd.core.utils.SecurtyUtil.sha512;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserDao userDao;
    private final UserAuthService userAuthService;
    private final UserHistoryService userHistoryService;
    private final UserLogService userLogService;
    private final MemberUtil memberUtil;

    public boolean checkPwReset(String userId) {
        UserDto user = userDao.findByUserId(userId).orElseGet(UserDto::new);
        return user.getUserPwResetYn() != null && user.getUserPwResetYn() == 'Y';
    }

    @Transactional
    public boolean updateUserPw(SetupUserInfoDto userDto) {
        UserDto user = userDao.findById(userDto.getUserId()).orElseGet(UserDto::new);

        if ( StringUtils.isEmpty(user.getUserId()) ) {
            return false;
        }

        final String descriptionPw = sha512(userDto.getUserPw());

        user.setUserPw( descriptionPw );
        user.setUserPwResetYn('N');

        userDao.save(user);

        return true;
    }

    public List<UserDto> getAllUserInfoWithDept() {
        return userDao.findAllDeptAndUser()
                .stream()
                .map(map -> {
                    UserDto u = userDao.mapToUserDto(map); // Assuming I add this helper or handle it
                    String company = (String) map.get("company");
                    String deptNm = (String) map.get("deptNm");
                    u.setCompany(company);
                    u.setDeptNm(deptNm);
                    return u;
                })
                .collect(Collectors.toList());
    }

    //public UserDetailDto getUserInfoWithDept(final String userId) {
    public UserDetailDto getUserInfoWithDept(Map<String, Object> mapParam) {

        String userId = (String) mapParam.get("userId");
        String currentUserId = memberUtil.getUserId();
    
        if (!userId.equals(currentUserId)) {
            userLogService.saveUserDetailLog(
                UserLogService.ACCESS_TYPE_MENU,
                userId,
                "userNm,userEmail,userPhoneMobile"
            );
        }  
        return userDao.findByUserWithDept(userId)
                .map(map -> {
                    UserDto u = mapToUserDto(map);
                    DepartmentStructureDto dept = new DepartmentStructureDto();
                    dept.setCompanyNm((String) map.get("companyNm"));
                    dept.setDepartmentNm((String) map.get("departmentNm"));

                    UserDetailDto userDetailDto = userDao.from(u, dept);
                    
                    List<String> mgrCompanyList = userDao.findMgrCompCodesByUserId(userId);
                    String mgrCompanyNm = userDao.findMgrCompNamesByUserId(userId);
                    
                    userDetailDto.setMgrCompanyList(mgrCompanyList);
                    userDetailDto.setMgrCompanyNm(mgrCompanyNm);
                    
                    return userDetailDto;
                })
                .orElseGet(UserDetailDto::new);
    }

    private UserDto mapToUserDto(Map<String, Object> map) {
        return userDao.mapToUserDto(map);
    }

    @Transactional
    public UserDto updateUserInfo(ChangeUserInfoDto requestUser) {
        UserDto user = userDao.findById(requestUser.getUserId()).orElseGet(UserDto::new);

        if (StringUtils.isEmpty(user.getUserId())) {
            throw new BizException("No user found.");
        }

        if ('Y' == user.getDeleteYn()) {
            String encodedOriginalEmail = user.getUserCheckCd();
            String encodedNewEmail = SecurtyUtil.sha256(requestUser.getUserEmail());

            if (userDao.existsByUserEmailAndDeleteYn(requestUser.getUserEmail(), 'N')) {
                throw new BizException("Email already in use.");
            }

            boolean isEmailChanged = encodedOriginalEmail != null && !encodedOriginalEmail.equals(encodedNewEmail);
        
            userHistoryService.reCreateUserHistory(user, requestUser, isEmailChanged);
            
            user.setDeleteYn('N');
            user.setUserCertYn('N');
            user.setUserConsentYn('N');
            user.setUserCheckCd(null);
            
            user.setUserEmail(requestUser.getUserEmail());
            
            userAuthService.storeUserDefaultAuth(user.getUserId(), requestUser.getModId(), requestUser.getCompanyTypeCd());
        } else {
            userHistoryService.addUserHistory(user, requestUser);
        }

        user.setUserDeptNm(requestUser.getUserDeptNm());
        user.setRemark(requestUser.getRemark());
        user.setUserPhoneMobile(requestUser.getUserPhoneMobile());
        user.setUserPhoneOffice(requestUser.getUserPhoneOffice());
        user.setUserPositionNm(requestUser.getUserPositionNm());
        user.setModId(requestUser.getModId());
        
        if (StringUtils.isNotEmpty(requestUser.getDeptCd())) {
            user.setDeptCd(requestUser.getDeptCd());
        }
        
        user.setModDt(LocalDateTime.now());

        userDao.save(user);

        userDao.deleteMgrCompByUserId(requestUser.getUserId());

        if (requestUser.getMgrCompanyList() != null && !requestUser.getMgrCompanyList().isEmpty()) {
            for (String compCd : requestUser.getMgrCompanyList()) {
                userDao.saveMgrComp(
                    requestUser.getUserId(),
                    compCd,
                    requestUser.getModId(),
                    LocalDateTime.now()
                );
            }
        }

        userLogService.saveUserDetailLog(
            UserLogService.ACCESS_TYPE_MENU,
            requestUser.getUserId(),
            "userNm,userEmail,userPhoneMobile"
        ); 
        
        return user;
    }   

    @Transactional
    public boolean setupUserInfo(SetupUserInfoDto userDto) {
        UserDto user = userDao.findById(userDto.getUserId()).orElseGet(UserDto::new);

        if ( StringUtils.isEmpty(user.getUserId()) ) {
            return false;
        }

        final String descriptionPw = sha512(userDto.getUserPw());

        user.setUserNm( userDto.getUserName() );
        user.setUserPw( descriptionPw );
        user.setUserPositionNm( userDto.getUserPosition() );
        user.setUserPhoneMobile( userDto.getUserPhoneMobile() );
        user.setUserPhoneOffice( userDto.getUserPhoneOffice() );
        user.setRemark( userDto.getRemark() );

        userDao.save(user);

        userLogService.saveUserDetailLog(
            UserLogService.ACCESS_TYPE_MENU,
            userDto.getUserId(),
            "userNm,userEmail,userPhoneMobile"
        ); 
        return true;
    }

    @Transactional
    public UserDto updateLoginUserInfo(UpdateLoginUserDto userInfoDto) {
        UserDto user = userDao.findById(userInfoDto.getUserId()).orElseGet(UserDto::new);

        if (StringUtils.isEmpty(user.getUserId())) {
            throw new BizException("No user found.");
        }

        user.setUserDeptNm(userInfoDto.getUserDeptNm());
        user.setUserPositionNm(userInfoDto.getUserPosition());
        user.setUserPhoneMobile(userInfoDto.getUserPhone());
        user.setUserPhoneOffice(userInfoDto.getUserPhoneOffice());
        user.setRemark(userInfoDto.getRemark());

        userDao.save(user);

        return user;
    }

    private String generateNewUserId() {
        Set<String> existingUserIds = userDao.findByUserIdStartingWith("USER");
        
        int sequence = 1;
        String newUserId;
        
        do {
            newUserId = String.format("USER%04d", sequence);
            sequence++;
            
            if (sequence > 9999) {
                throw new BizException("Exceeded max User ID limit.");
            }
        } while (existingUserIds.contains(newUserId));
        
        return newUserId;
    }
    
    @Transactional
    public UserDto registerUserInfo(UserRegistDto requestUser) {
        if (StringUtils.isEmpty(requestUser.getUserId())) {
            requestUser.setUserId(requestUser.getUserEmail());
        }

        final String userId = generateNewUserId();

        String encodedEmail = SecurtyUtil.sha256(requestUser.getUserEmail());
        Optional<UserDto> deletedUserOpt = userDao.findByUserCheckCdAndDeleteYn(encodedEmail, 'Y');

        if (deletedUserOpt.isPresent()) {
            UserDto deletedUser = deletedUserOpt.get();
            
            requestUser.setUserId(deletedUser.getUserId());

            userHistoryService.reCreateUserHistory(deletedUser, requestUser);
            
            deletedUser.setUserNm(requestUser.getUserNm());
            deletedUser.setUserEmail(requestUser.getUserEmail());
            deletedUser.setUserDeptNm(requestUser.getUserDeptNm());
            deletedUser.setDeptCd(requestUser.getDeptCd());
            deletedUser.setUserPositionNm(requestUser.getUserPositionNm());
            deletedUser.setUserPhoneMobile(requestUser.getUserPhoneMobile());
            deletedUser.setUserPhoneOffice(requestUser.getUserPhoneOffice());
            deletedUser.setRemark(requestUser.getRemark());
            
            deletedUser.setDeleteYn('N');
            deletedUser.setUserCertYn('N');
            deletedUser.setUserConsentYn('N');
            deletedUser.setUserCheckCd(null);
            
            deletedUser.setModId(requestUser.getRegId());
            deletedUser.setModDt(LocalDateTime.now());
            
            userDao.deleteMgrCompByUserId(deletedUser.getUserId());
            if (requestUser.getMgrCompanyList() != null && !requestUser.getMgrCompanyList().isEmpty()) {
                for (String compCd : requestUser.getMgrCompanyList()) {
                    userDao.saveMgrComp(
                        deletedUser.getUserId(),
                        compCd,
                        requestUser.getRegId(),
                        LocalDateTime.now()
                    );
                }
            }
            
            userDao.save(deletedUser);
            return deletedUser;
        }

        if (userDao.existsByUserEmail(requestUser.getUserEmail())) {
            throw new BizException("User already exists.");
        }

        UserDto user = new UserDto();

        user.setUserId(userId);
        user.setDeptCd(requestUser.getDeptCd());
        user.setUserNm(requestUser.getUserNm());
        user.setUserDeptNm(requestUser.getUserDeptNm());
        user.setUserEmail(requestUser.getUserEmail());
        user.setRemark(requestUser.getRemark());
        user.setUserPhoneMobile(requestUser.getUserPhoneMobile());
        user.setUserPhoneOffice(requestUser.getUserPhoneOffice());
        user.setUserPositionNm(requestUser.getUserPositionNm());

        user.setUserCertYn('N');
        user.setUserConsentYn('N');
        user.setDeleteYn('N');

        user.setRegId(requestUser.getRegId());
        user.setRegDt(LocalDateTime.now());

        userHistoryService.createUserHistory(user, requestUser);

        userAuthService.storeUserDefaultAuth(userId, requestUser.getRegId(), requestUser.getCompanyTypeCd());

        userDao.save(user);
        
        if (requestUser.getMgrCompanyList() != null && !requestUser.getMgrCompanyList().isEmpty()) {
            for (String compCd : requestUser.getMgrCompanyList()) {
                userDao.saveMgrComp(
                    userId,
                    compCd,
                    requestUser.getRegId(),
                    LocalDateTime.now()
                );
            }
        }

        return user;
    }

    @Transactional
    public List<UserRegistDto> multiRegisterUserInfo(List<UserRegistDto> userList) {
        List<UserRegistDto> failedRegisterUser = new ArrayList<>();
        List<UserDto> users = new ArrayList<>();
        Set<String> processedEmails = new HashSet<>();

        for (var user : userList) {
            if (StringUtils.isEmpty(user.getUserEmail())) {
                failedRegisterUser.add(user);
                continue;
            }

            String email = user.getUserEmail();
            
            if (processedEmails.contains(email)) {
                continue;
            }

            UserDto entity;
            String userId;

            if (userDao.existsByUserEmail(email)) {
                Optional<UserDto> activeUserOpt = userDao.findByUserEmail(email);
                
                entity = activeUserOpt.get();
                userId = entity.getUserId();
                
                ChangeUserInfoDto changeDto = new ChangeUserInfoDto();
                changeDto.setUserId(userId);
                changeDto.setDeptCd(user.getDeptCd());
                changeDto.setUserDeptNm(user.getUserDeptNm());
                changeDto.setUserPositionNm(user.getUserPositionNm());
                changeDto.setUserPhoneMobile(user.getUserPhoneMobile());
                changeDto.setUserPhoneOffice(user.getUserPhoneOffice());
                changeDto.setRemark(user.getRemark());
                changeDto.setModId(user.getRegId() != null ? user.getRegId() : userId);
                
                userHistoryService.addUserHistory(entity, changeDto);
                
                entity.setUserNm(user.getUserNm());
                entity.setDeptCd(user.getDeptCd());
                entity.setUserDeptNm(user.getUserDeptNm());
                entity.setUserPositionNm(user.getUserPositionNm());
                entity.setUserPhoneMobile(user.getUserPhoneMobile());
                entity.setUserPhoneOffice(user.getUserPhoneOffice());
                entity.setRemark(user.getRemark());
                entity.setModDt(LocalDateTime.now());
                entity.setModId(user.getRegId() != null ? user.getRegId() : userId);
                
                user.setUserId(userId);
                
            } else {
                String encodedEmail = SecurtyUtil.sha256(email);
                Optional<UserDto> deletedUserOpt = userDao.findByUserCheckCdAndDeleteYn(encodedEmail, 'Y');
                
                if (deletedUserOpt.isPresent()) {
                    entity = deletedUserOpt.get();
                    userId = entity.getUserId();
                    
                    user.setUserId(userId);

                    ChangeUserInfoDto changeDto = new ChangeUserInfoDto();
                    changeDto.setUserId(userId);
                    changeDto.setDeptCd(user.getDeptCd());
                    changeDto.setUserDeptNm(user.getUserDeptNm());
                    changeDto.setUserPositionNm(user.getUserPositionNm());
                    changeDto.setUserPhoneMobile(user.getUserPhoneMobile());
                    changeDto.setUserPhoneOffice(user.getUserPhoneOffice());
                    changeDto.setRemark(user.getRemark());
                    changeDto.setModId(user.getRegId() != null ? user.getRegId() : userId);

                    String encodedOriginalEmail = entity.getUserCheckCd();
                    String encodedNewEmail = SecurtyUtil.sha256(email);

                    boolean isEmailChanged = encodedOriginalEmail != null && !encodedOriginalEmail.equals(encodedNewEmail);

                    userHistoryService.reCreateUserHistory(entity, changeDto, isEmailChanged);

                    entity.setUserEmail(email);
                    entity.setUserNm(user.getUserNm());
                    entity.setDeptCd(user.getDeptCd());
                    entity.setUserDeptNm(user.getUserDeptNm());
                    entity.setUserPositionNm(user.getUserPositionNm());
                    entity.setUserPhoneMobile(user.getUserPhoneMobile());
                    entity.setUserPhoneOffice(user.getUserPhoneOffice());
                    entity.setRemark(user.getRemark());
                    
                    entity.setDeleteYn('N');
                    entity.setUserCertYn('N');
                    entity.setUserConsentYn('N');
                    entity.setUserCheckCd(null);
                    
                    entity.setModDt(LocalDateTime.now());
                    entity.setModId(user.getRegId() != null ? user.getRegId() : userId);
                    
                    user.setUserId(userId);
                    
                } else {
                    userId = generateNewUserId();
                    
                    entity = new UserDto();
                    entity.setUserId(userId);
                    entity.setUserNm(user.getUserNm());
                    entity.setUserEmail(user.getUserEmail());
                    entity.setDeptCd(user.getDeptCd());
                    entity.setUserDeptNm(user.getUserDeptNm());
                    entity.setUserPositionNm(user.getUserPositionNm());
                    entity.setUserPhoneMobile(user.getUserPhoneMobile());
                    entity.setUserPhoneOffice(user.getUserPhoneOffice());
                    entity.setRemark(user.getRemark());
                    
                    entity.setUserCertYn('N');
                    entity.setUserConsentYn('N');
                    entity.setDeleteYn('N');
                    entity.setRegDt(LocalDateTime.now());
                    entity.setRegId(user.getRegId() != null ? user.getRegId() : userId);
                    
                    user.setUserId(userId);

                    userHistoryService.createUserHistory(entity, user);
                }
            }

            users.add(entity);
            processedEmails.add(email);

            String companyTypeCd = StringUtils.isEmpty(user.getCompanyTypeCd()) ? "C" : user.getCompanyTypeCd();
            String regId = StringUtils.isEmpty(user.getRegId()) ? userId : user.getRegId();
            userAuthService.storeUserDefaultAuth(userId, regId, companyTypeCd);
        }

        userDao.saveAll(users);
        return failedRegisterUser;
    }

    public boolean checkDuplicateEmail(UserDuplicateDto userInfo) {
        return userDao.existsByUserEmailAndDeleteYn(userInfo.getUserEmail(), 'N');
    }

    public void removeUsers(UserRemoveDto userRemoveDto) {
        List<String> userIds = userRemoveDto.getUserIds();

        if (!userIds.isEmpty()) {
            userDao.deleteByIds(userIds);
        }
    }

    public List<Map<String, Object>> getAuthGroupUser(String userId) {
        return userDao.getAuthGroupUser(userId);
    }

    public List<Map<String, Object>> getRequesterByEmail(String userEmail) {
        return userDao.getRequesterByEmail(userEmail);
    }

    @Transactional
    public void softDeleteUsers(UserDeleteDto dto) {
        for (String userId : dto.getUserIds()) {
            UserDto user = userDao.findById(userId)
                    .orElseThrow(() -> new BizException(userId + " not found."));

            dto.setUserId(userId);
            userHistoryService.deleteUserHistory(user, dto);

            String encoded = SecurtyUtil.sha256(user.getUserEmail());

            user.setUserEmail(null);
            user.setUserPhoneMobile(null);
            user.setUserPhoneOffice(null);
            user.setUserCertYn('N');
            user.setUserConsentYn('N');
            user.setDeleteYn('Y');
            user.setUserCheckCd(encoded);

            userDao.save(user);
        }
    }

}
