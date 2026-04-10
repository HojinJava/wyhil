package com.hnix.sd.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkDao {

	List<Map<String, Object>> getSubCode(Map<String, String> map);

	List<Map<String, Object>> findWorkRegistrationByServiceNo(Map<String, String> map);

	List<Map<String, Object>> getWorkList(Map<String, Object> params);

	List<Map<String, Object>> getWorkDetail(Map<String, Object> params);

	List<Map<String, Object>> getReportPartner(Map<String, Object> params);

	List<Map<String, Object>> getReportSubcode(Map<String, Object> params);

	List<Map<String, Object>> getReportSubcodeDetail(Map<String, Object> params);

}


