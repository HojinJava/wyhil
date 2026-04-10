package com.hnix.sd.work.dashboard.dao;

import com.hnix.sd.work.dashboard.dto.DashboardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardDao {

    List<Object[]> findByCompCdAndYearAndMonth(@Param("dto") DashboardDto dto);

    List<Object[]> findBySearchYear(@Param("searchYear") String searchYear);

    List<Object[]> findByCompanyCd();
}
