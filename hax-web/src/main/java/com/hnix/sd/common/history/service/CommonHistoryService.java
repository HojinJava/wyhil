package com.hnix.sd.common.history.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.hnix.sd.common.history.dao.CommonHistoryDao;
import com.hnix.sd.common.history.dto.CommonHistoryDto;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.file.dto.FileDto;
import com.hnix.sd.common.file.service.FileService;
import com.hnix.sd.core.utils.MemberUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommonHistoryService {
    private final CommonHistoryDao commonHistoryDao;
    private final MemberUtil memberUtil;
    private final UserDao userDao;
    private final FileService fileService;

    public void addCommonHistory (CommonHistoryDto commonHistoryDto) {
        UserDto user = userDao.findByUserId(memberUtil.getUserId()).orElseGet(UserDto::new);
        
        commonHistoryDto.setHisDt(LocalDateTime.now());
        commonHistoryDto.setUserId(user.getUserId());

        commonHistoryDao.insert(commonHistoryDto);
    }

    public List<CommonHistoryDto> getCommonHistory(String targetId, String menuCd) {
//        List<CommonHistoryDto> commonHistoryList = commonHistoryDao.findByTargetIdAndMenuCd(targetId, menuCd);
//
//        for (CommonHistoryDto history : commonHistoryList) {
//            String fileId = history.getFileId();
//
//            List<FileDto> fileList = fileService.getFileList(fileId);
//            history.setFileList(fileList);
//        }
//        return commonHistoryList;

        return commonHistoryDao.findByTargetIdAndMenuCd(targetId, menuCd);
    }
}
