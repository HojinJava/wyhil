package com.hnix.sd.common.user.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hnix.sd.common.history.CommonHistoryUtil;
import com.hnix.sd.common.history.dto.CommonHistoryDto;
import com.hnix.sd.common.history.dao.CommonHistoryDao;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.dto.ChangeUserInfoDto;
import com.hnix.sd.common.user.dto.UserDeleteDto;
import com.hnix.sd.common.user.dto.UserRegistDto;
import com.hnix.sd.common.history.service.CommonHistoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserHistoryService {
	private final CommonHistoryService commonHistoryService;
    private final CommonHistoryDao commonHistoryDao;
	private final UserDao userDao;

	public void createUserHistory(UserDto user, UserRegistDto registeDto) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_CREATE;
		String msg = "";
	
		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(registeDto.getUserId());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(registeDto.getRegId());
	
		commonHistoryService.addCommonHistory(commonHistoryDto);
	}

	public void reCreateUserHistory(UserDto user, ChangeUserInfoDto requestUser, boolean isEmailChanged) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_RECREATE;
		String msg = "";
	
		if (requestUser.getDeptCd() != null && !StringUtils.equals(user.getDeptCd(), requestUser.getDeptCd())) {
			String oldCompanyNm = userDao.findCompanyNameByDeptCd(user.getDeptCd());
			String oldDeptNm = userDao.findDepartmentNameByDeptCd(user.getDeptCd());
			String oldCompanyFull = (oldCompanyNm != null ? oldCompanyNm : "") + "/" + 
									(oldDeptNm != null ? oldDeptNm : "");

			String newCompanyNm = userDao.findCompanyNameByDeptCd(requestUser.getDeptCd());
			String newDeptNm = userDao.findDepartmentNameByDeptCd(requestUser.getDeptCd());
			String newCompanyFull = (newCompanyNm != null ? newCompanyNm : "") + "/" + 
									(newDeptNm != null ? newDeptNm : "");

			msg = CommonHistoryUtil.getCommonHistory(msg, "Company/Dept", 
				oldCompanyFull, newCompanyFull);
		}
		
		String oldMgrCompanyNm = userDao.findMgrCompNamesByUserId(user.getUserId());
		String newMgrCompanyNm = requestUser.getMgrCompanyNm();

		if (StringUtils.isNotEmpty(newMgrCompanyNm) && !StringUtils.equals(oldMgrCompanyNm, newMgrCompanyNm)) {
			msg = CommonHistoryUtil.getCommonHistory(msg, "Mgr Company", 
				oldMgrCompanyNm != null ? oldMgrCompanyNm : "", 
				newMgrCompanyNm);
		}
		
		if (isEmailChanged) {
			msg = CommonHistoryUtil.getCommonHistory(msg, "Email", "", requestUser.getUserEmail());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserDeptNm(), requestUser.getUserDeptNm())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Dept Name", user.getUserDeptNm(), requestUser.getUserDeptNm());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPositionNm(), requestUser.getUserPositionNm())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Position", user.getUserPositionNm(), requestUser.getUserPositionNm());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPhoneMobile(), requestUser.getUserPhoneMobile())) {
			String oldValue = user.getUserPhoneMobile();
			String newValue = requestUser.getUserPhoneMobile();

    		if (!(StringUtils.isEmpty(oldValue) && StringUtils.isEmpty(newValue))) {
				msg = CommonHistoryUtil.getCommonHistory(msg, "Mobile", 
					StringUtils.defaultString(oldValue), 
					StringUtils.defaultString(newValue));
			}
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPhoneOffice(), requestUser.getUserPhoneOffice())) {
			String oldValue = user.getUserPhoneOffice();
			String newValue = requestUser.getUserPhoneOffice();
			
			if (!(StringUtils.isEmpty(oldValue) && StringUtils.isEmpty(newValue))) {
				msg = CommonHistoryUtil.getCommonHistory(msg, "Office Phone", 
					StringUtils.defaultString(oldValue), 
					StringUtils.defaultString(newValue));
			}
		}

		if (CommonHistoryUtil.isOtherValue(user.getRemark(), requestUser.getRemark())) {
			String oldRemark = StringUtils.defaultString(user.getRemark());
			String newRemark = StringUtils.defaultString(requestUser.getRemark());
			
			if (!oldRemark.equals(newRemark)) {
				msg = CommonHistoryUtil.getCommonHistory(msg, "Remark", oldRemark, newRemark);
			}
		}

		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(requestUser.getUserId());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(requestUser.getModId());

		commonHistoryService.addCommonHistory(commonHistoryDto);
	}
	
	public void reCreateUserHistory(UserDto user, UserRegistDto registeDto) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_RECREATE;
		String msg = "";

		if (registeDto.getDeptCd() != null && !StringUtils.equals(user.getDeptCd(), registeDto.getDeptCd())) {
			String oldCompanyNm = userDao.findCompanyNameByDeptCd(user.getDeptCd());
			String oldDeptNm = userDao.findDepartmentNameByDeptCd(user.getDeptCd());
			String oldCompanyFull = (oldCompanyNm != null ? oldCompanyNm : "") + "/" + 
									(oldDeptNm != null ? oldDeptNm : "");

			String newCompanyNm = userDao.findCompanyNameByDeptCd(registeDto.getDeptCd());
			String newDeptNm = userDao.findDepartmentNameByDeptCd(registeDto.getDeptCd());
			String newCompanyFull = (newCompanyNm != null ? newCompanyNm : "") + "/" + 
									(newDeptNm != null ? newDeptNm : "");

			msg = CommonHistoryUtil.getCommonHistory(msg, "Company/Dept", 
				oldCompanyFull, newCompanyFull);
		}
		
		String oldMgrCompanyNm = userDao.findMgrCompNamesByUserId(user.getUserId());
		String newMgrCompanyNm = registeDto.getMgrCompanyNm();

		if (StringUtils.isNotEmpty(newMgrCompanyNm) && !StringUtils.equals(oldMgrCompanyNm, newMgrCompanyNm)) {
			msg = CommonHistoryUtil.getCommonHistory(msg, "Mgr Company", 
				oldMgrCompanyNm != null ? oldMgrCompanyNm : "", 
				newMgrCompanyNm);
		}
		
		if (CommonHistoryUtil.isOtherValue(user.getUserDeptNm(), registeDto.getUserDeptNm())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Dept Name", user.getUserDeptNm(), registeDto.getUserDeptNm());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPositionNm(), registeDto.getUserPositionNm())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Position", user.getUserPositionNm(), registeDto.getUserPositionNm());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPhoneMobile(), registeDto.getUserPhoneMobile())) {
			String oldValue = user.getUserPhoneMobile();
			String newValue = registeDto.getUserPhoneMobile();

    		if (!(StringUtils.isEmpty(oldValue) && StringUtils.isEmpty(newValue))) {
				msg = CommonHistoryUtil.getCommonHistory(msg, "Mobile", 
					StringUtils.defaultString(oldValue), 
					StringUtils.defaultString(newValue));
			}
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPhoneOffice(), registeDto.getUserPhoneOffice())) {
			String oldValue = user.getUserPhoneOffice();
			String newValue = registeDto.getUserPhoneOffice();
			
			if (!(StringUtils.isEmpty(oldValue) && StringUtils.isEmpty(newValue))) {
				msg = CommonHistoryUtil.getCommonHistory(msg, "Office Phone", 
					StringUtils.defaultString(oldValue), 
					StringUtils.defaultString(newValue));
			}
		}

		if (CommonHistoryUtil.isOtherValue(user.getRemark(), registeDto.getRemark())) {
			String oldRemark = StringUtils.defaultString(user.getRemark());
			String newRemark = StringUtils.defaultString(registeDto.getRemark());
			
			if (!oldRemark.equals(newRemark)) {
				msg = CommonHistoryUtil.getCommonHistory(msg, "Remark", oldRemark, newRemark);
			}
		}

		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(registeDto.getUserId());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(registeDto.getRegId());
		
		commonHistoryService.addCommonHistory(commonHistoryDto);
	}

	public void addUserHistory(UserDto user, ChangeUserInfoDto requestUser) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_UPDATE;

		String msg = "";

		if (requestUser.getDeptCd() != null && !StringUtils.equals(user.getDeptCd(), requestUser.getDeptCd())) {
			String oldCompanyNm = userDao.findCompanyNameByDeptCd(user.getDeptCd());
			String oldDeptNm = userDao.findDepartmentNameByDeptCd(user.getDeptCd());
			String oldCompanyFull = (oldCompanyNm != null ? oldCompanyNm : "") + "/" + 
									(oldDeptNm != null ? oldDeptNm : "");

			String newCompanyNm = userDao.findCompanyNameByDeptCd(requestUser.getDeptCd());
			String newDeptNm = userDao.findDepartmentNameByDeptCd(requestUser.getDeptCd());
			String newCompanyFull = (newCompanyNm != null ? newCompanyNm : "") + "/" + 
									(newDeptNm != null ? newDeptNm : "");

			msg = CommonHistoryUtil.getCommonHistory(msg, "Company/Dept", 
				oldCompanyFull, newCompanyFull);
		}
		
		String oldMgrCompanyNm = userDao.findMgrCompNamesByUserId(user.getUserId());
		String newMgrCompanyNm = requestUser.getMgrCompanyNm();

		String normalizedOld = StringUtils.isEmpty(oldMgrCompanyNm) ? null : oldMgrCompanyNm;
		String normalizedNew = StringUtils.isEmpty(newMgrCompanyNm) ? null : newMgrCompanyNm;

		if (!StringUtils.equals(normalizedOld, normalizedNew)) {
			msg = CommonHistoryUtil.getCommonHistory(msg, "Mgr Company", 
				normalizedOld != null ? normalizedOld : "", 
				normalizedNew != null ? normalizedNew : "");
		}
		
		if (CommonHistoryUtil.isOtherValue(user.getUserDeptNm(), requestUser.getUserDeptNm())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Dept Name", user.getUserDeptNm(), requestUser.getUserDeptNm());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPositionNm(), requestUser.getUserPositionNm())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Position", user.getUserPositionNm(), requestUser.getUserPositionNm());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPhoneMobile(), requestUser.getUserPhoneMobile())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Mobile", user.getUserPhoneMobile(), requestUser.getUserPhoneMobile());
		}

		if (CommonHistoryUtil.isOtherValue(user.getUserPhoneOffice(), requestUser.getUserPhoneOffice())) {
    		msg = CommonHistoryUtil.getCommonHistory(msg, "Office Phone", user.getUserPhoneOffice(), requestUser.getUserPhoneOffice());
		}

		if (CommonHistoryUtil.isOtherValue(user.getRemark(), requestUser.getRemark())) {
			String oldRemark = StringUtils.defaultString(user.getRemark());
			String newRemark = StringUtils.defaultString(requestUser.getRemark());
			
			if (!oldRemark.equals(newRemark)) {
				msg = CommonHistoryUtil.getCommonHistory(msg, "Remark", oldRemark, newRemark);
			}
		}

		if (StringUtils.isNotEmpty(msg)) {
			CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
			commonHistoryDto.setTargetId(requestUser.getUserId());
			commonHistoryDto.setMenuCd(menuCd);
			commonHistoryDto.setHisTypeCd(hisType);
			commonHistoryDto.setHisContents(msg);
			commonHistoryDto.setUserId(requestUser.getModId());

			commonHistoryService.addCommonHistory(commonHistoryDto);
		}
	}

	public void deleteUserHistory(UserDto user, UserDeleteDto requestUser) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_DELETE;

		String msg = "";

		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(requestUser.getUserId());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(requestUser.getModId());

		commonHistoryService.addCommonHistory(commonHistoryDto);
	}
	
	public void consentUserHistory(UserDto user, ChangeUserInfoDto requestUser) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_CONSENT;

		String msg = "";


		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(requestUser.getUserId());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(requestUser.getModId());

		commonHistoryService.addCommonHistory(commonHistoryDto);
	}
	
	public void certUserHistory(UserDto user, ChangeUserInfoDto requestUser) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_CERT;

		String msg = "";


		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(requestUser.getUserId());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(requestUser.getModId());

		commonHistoryService.addCommonHistory(commonHistoryDto);
	}

	@Transactional
	public void consentUserExpireHistory(UserDto user, ChangeUserInfoDto requestUser) {
		String menuCd = "common-dept-user";
		String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_CONSENT_EX;

		String msg = "";

		CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
		commonHistoryDto.setTargetId(requestUser.getUserId());
		commonHistoryDto.setMenuCd(menuCd);
		commonHistoryDto.setHisTypeCd(hisType);
		commonHistoryDto.setHisContents(msg);
		commonHistoryDto.setUserId(requestUser.getModId());

        commonHistoryDao.insertCommonHistory(commonHistoryDto);
	}	
}