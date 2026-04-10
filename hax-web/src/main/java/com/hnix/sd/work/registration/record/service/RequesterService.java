package com.hnix.sd.work.registration.record.service;

import com.hnix.sd.work.registration.record.dto.RequesterUserDto;
import com.hnix.sd.common.user.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RequesterService {

    private final UserDao userDao;

    //요청자 호출
    public List<RequesterUserDto> getRequesterByDeptCd(final String deptCode, final Character isRequester) {
        return userDao.getRequesterByDeptCd(deptCode, isRequester != null ? isRequester.toString() : "N");
    }

}
