package com.hnix.sd.common.excel.dao;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ExcelDao {
    List<Map<String, Object>> getReportSubcode(Map<String, Object> params);
    List<Map<String, Object>> getWorkList(Map<String, Object> params);
    List<Map<String, Object>> getReportPartner(Map<String, Object> params);

    List<com.hnix.sd.common.excel.dto.WorkRegistrationExcelFieldsDto> getWorkRegistrationExcel(Map<String, Object> params);

    List<com.hnix.sd.common.excel.dto.WorkResultExcelFieldsDto> getWorkResultExcel(Map<String, Object> params);
}
