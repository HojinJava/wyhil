package com.hnix.sd.common.log.dao;

import com.hnix.sd.common.log.dto.AccessLogInfoDto;
import com.hnix.sd.common.log.dto.TcUserAccessLogDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TcAccessLogDao {

    int countAccessLogs(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<AccessLogInfoDto> getAccessLogs(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    void insertAccessLog(TcUserAccessLogDto logDto);
}
