package com.hnix.sd.common.user.service;

import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserGridDto;
import com.hnix.sd.common.user.dto.UserSearchPageDto;
import com.hnix.sd.core.dto.PageRequestDto;
import com.hnix.sd.core.dto.PageResponseDto;
import com.hnix.sd.core.utils.PageableUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserPageService {

    private final UserDao userDao;
    private final UserLogService userLogService;

    public PageResponseDto<UserGridDto> getUserWithDeptPagination(int page, int size, String sort, String deptCd, String userNm, String userEmail) {
        PageRequestDto pageRequest = PageableUtil.createPageRequest(page, size, sort);
        
        UserSearchPageDto searchDto = new UserSearchPageDto();
        searchDto.setDeptCd(deptCd);
        searchDto.setUserNm(userNm);
        searchDto.setUserEmail(userEmail);
        searchDto.setOffset(pageRequest.getOffset());
        searchDto.setLimit(pageRequest.getLimit());
        searchDto.setPageSize(size);
        
        if (pageRequest.getSort() != null) {
            searchDto.setCriteria(pageRequest.getSort());
            searchDto.setSort(pageRequest.getDirection());
            searchDto.setDirection(pageRequest.getDirection());
        }

        List<UserGridDto> userList = userDao.findUserGridPagination(searchDto);
        long count = userDao.countUserGridPagination(searchDto);

        userLogService.saveUserListLog(
            UserLogService.ACCESS_TYPE_MENU,
            "common-dept-user",
            deptCd,
            userNm,
            userEmail
        );

        return new PageResponseDto<>(userList, pageRequest, count);
    }

    // 목록 전체 조회
    public List<UserGridDto> getAllUsers(String deptCd, String userNm, String userEmail) {
        UserSearchPageDto searchDto = new UserSearchPageDto();
        searchDto.setDeptCd(deptCd);
        searchDto.setUserNm(userNm);
        searchDto.setUserEmail(userEmail);
        
        return userDao.findAllUserGrid(searchDto);
    }

}
