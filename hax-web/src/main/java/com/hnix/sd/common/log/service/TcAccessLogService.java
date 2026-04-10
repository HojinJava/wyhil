package com.hnix.sd.common.log.service;

import com.hnix.sd.common.log.dao.TcAccessLogDao;
import com.hnix.sd.common.log.dto.AccessLogInfoDto;
import com.hnix.sd.common.log.dto.AccessLogPaginationDto;
import com.hnix.sd.common.log.dto.SearchAccessLogDto;
import com.hnix.sd.common.log.dto.TcUserAccessLogDto;
import com.hnix.sd.core.utils.MemberUtil;
import com.hnix.sd.core.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TcAccessLogService {

    private final TcAccessLogDao tcAccessLogDao;
    private final MemberUtil memberUtil;
    private final RequestUtil requestUtil;

    public AccessLogPaginationDto getAccessLogByPagination(SearchAccessLogDto logPaginationDto) {
        final Integer pageNo = logPaginationDto.getPageNo() != null ? logPaginationDto.getPageNo() : 0;
        final Integer pageSize = logPaginationDto.getPageSize() != null ? logPaginationDto.getPageSize() : 10;

        LocalDate startDate = logPaginationDto.getStartDate();
        LocalDate endDate = logPaginationDto.getEndDate();

        if (startDate == null || endDate == null) {
            LocalDate currentDate = LocalDate.now();
            endDate = currentDate;
            startDate = currentDate.minusMonths(3L);
        }

        int offset = pageNo * pageSize;
        int totalElements = tcAccessLogDao.countAccessLogs(startDate, endDate.plusDays(1L));
        List<AccessLogInfoDto> elements = tcAccessLogDao.getAccessLogs(startDate, endDate.plusDays(1L), offset, pageSize);

        AccessLogPaginationDto logPageDto = new AccessLogPaginationDto();
        logPageDto.setPageSize(pageSize);
        logPageDto.setPageNumber(pageNo);
        logPageDto.setTotalPages((int) Math.ceil((double) totalElements / pageSize));
        logPageDto.setTotalElements((long) totalElements);
        logPageDto.setElements(elements);

        return logPageDto;
    }

    public void insertAccessLog(String menuCd, String menuType) {
        TcUserAccessLogDto dto = new TcUserAccessLogDto();
        dto.setAccessDt(LocalDateTime.now());
        dto.setMenuCd(menuCd);
        dto.setMenuType(menuType);
        dto.setUserId(memberUtil.getUserId());
        dto.setUserIp(requestUtil.getIp());
        dto.setUserSessionId(requestUtil.getSessionId());

        tcAccessLogDao.insertAccessLog(dto);
    }
}
