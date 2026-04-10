package com.hnix.sd.work.software.history.dao;

import com.hnix.sd.work.software.history.dto.HistoryGridInfoDto;
import com.hnix.sd.work.software.history.dto.HistorySearchDto;
import com.hnix.sd.work.software.history.dto.SoftwareHistoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SoftwareHistoryDao {

    Optional<SoftwareHistoryDto> findById(@Param("historyId") String historyId);

    List<SoftwareHistoryDto> findByContractNo(@Param("contractNo") String contractNo);

    int countByContractNo(@Param("contractNo") String contractNo);

    void insertHistory(SoftwareHistoryDto history);

    void updateHistory(SoftwareHistoryDto history);

    void updateHistoryUseYn(@Param("historyId") String historyId);

    List<HistoryGridInfoDto> findSoftwareHistoryByPagination(HistorySearchDto search);

    long countSoftwareHistoryByPagination(HistorySearchDto search);
}
