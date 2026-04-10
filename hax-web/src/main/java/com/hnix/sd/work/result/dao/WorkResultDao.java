package com.hnix.sd.work.result.dao;

import com.hnix.sd.common.excel.dto.WorkResultExcelFieldsDto;
import com.hnix.sd.work.result.dto.SearchWorkResultDto;
import com.hnix.sd.work.result.dto.WorkResultGridDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkResultDao {

    List<WorkResultGridDto> findWorkResultPagination(SearchWorkResultDto searchDto);

    long countWorkResultPagination(SearchWorkResultDto searchDto);

    List<WorkResultExcelFieldsDto> findWorkResultWithExcel(SearchWorkResultDto searchDto);
}
