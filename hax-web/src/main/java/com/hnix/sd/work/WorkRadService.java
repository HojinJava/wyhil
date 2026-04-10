package com.hnix.sd.work;

import com.hnix.sd.dao.CommonDao;
import com.hnix.sd.dao.WorkDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class WorkRadService {

    private final WorkDao workDao;
    private final CommonDao commonDao;

    public List<Map<String, Object>> getWorkList(Map<String, Object> params) {
        return workDao.getWorkList(params);
    }

    public List<Map<String, Object>> getWorkDetail(Map<String, Object> params) {
        return workDao.getWorkDetail(params);
    }

    public List<Map<String, Object>> getReportPartner(Map<String, Object> params) {
        return workDao.getReportPartner(params);
    }

    public List<Map<String, Object>> getReportSubcode(Map<String, Object> params) {
        return workDao.getReportSubcode(params);
    }

    public List<Map<String, Object>> getReportSubcodeDetail(Map<String, Object> params) {
        return workDao.getReportSubcodeDetail(params);
    }

    public List<Map<String, Object>> getSubCode(Map<String, Object> params) {
        Map<String, String> stringParams = new HashMap<>();
        params.forEach((k, v) -> stringParams.put(k, v != null ? v.toString() : null));
        return workDao.getSubCode(stringParams);
    }

    public List<Map<String, Object>> getCommonCode(Map<String, Object> params) {
        Map<String, String> stringParams = new HashMap<>();
        params.forEach((k, v) -> stringParams.put(k, v != null ? v.toString() : null));
        return commonDao.getCommonCode(stringParams);
    }
}
