package com.hnix.sd.common.history.dao;

import com.hnix.sd.common.history.dto.CommonHistoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommonHistoryDao {
    void insert(CommonHistoryDto history);
    void insertCommonHistory(CommonHistoryDto history);
    List<CommonHistoryDto> findByTargetIdAndMenuCd(@Param("targetId") String targetId, @Param("menuCd") String menuCd);
    void deleteByTargetIdAndMenuCd(@Param("targetId") String targetId, @Param("menuCd") String menuCd);
    void deleteByMenuCdAndTargetIdIn(@Param("menuCd") String menuCd, @Param("targetIds") List<String> targetIds);
}
