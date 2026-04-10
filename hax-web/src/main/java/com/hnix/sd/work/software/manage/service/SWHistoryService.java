package com.hnix.sd.work.software.manage.service;

import com.hnix.sd.common.history.dao.CommonHistoryDao;
import com.hnix.sd.common.history.CommonHistoryUtil;
import com.hnix.sd.common.history.dto.CommonHistoryDto;
import com.hnix.sd.work.software.manage.dto.SoftwareDto;
import com.hnix.sd.work.software.manage.dto.SoftwareInfoDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SWHistoryService {
    private final CommonHistoryDao commonHistoryDao;

    public void addSoftwareHistory(SoftwareDto software, SoftwareInfoDto softwareInfoDto) {
        String menuCd = "work-software-manage";
        String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_UPDATE;
        String msg = "";
        
        if(software == null || StringUtils.isEmpty(software.getSwCode())) {
            hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_CREATE;
            msg = "";
        } else {
            if (CommonHistoryUtil.isOtherValue(software.getSwName(), softwareInfoDto.getSwName())) {
                msg = CommonHistoryUtil.getCommonHistory(msg, "이름", software.getSwName(), softwareInfoDto.getSwName());
            }
            
            if (CommonHistoryUtil.isOtherValue(software.getSwDesc(), softwareInfoDto.getSwDesc())) {
                msg = CommonHistoryUtil.getCommonHistory(msg, "설명", software.getSwDesc(), softwareInfoDto.getSwDesc());
            }
        }

        CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
        commonHistoryDto.setTargetId(softwareInfoDto.getSwCode());
        commonHistoryDto.setMenuCd(menuCd);
        commonHistoryDto.setHisTypeCd(hisType);
        commonHistoryDto.setHisContents(msg);
        commonHistoryDto.setUserId(softwareInfoDto.getModId() != null ? softwareInfoDto.getModId() : softwareInfoDto.getRegId()); 
        
        commonHistoryDao.insert(commonHistoryDto);
    }
}


