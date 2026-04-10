package com.hnix.sd.common.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hnix.sd.common.user.dto.UserLogDto;
import com.hnix.sd.common.user.dao.UserLogDao;
import com.hnix.sd.core.utils.MemberUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLogService {

	private final UserLogDao userLogDao;
	private final MemberUtil memberUtil;

	public static final String ACCESS_TYPE_LOGIN = "LOG_ACCESS_TYPE_LOGIN";
    public static final String ACCESS_TYPE_MENU = "LOG_ACCESS_TYPE_MENU";
    public static final String ACCESS_TYPE_MAILLINK = "LOG_ACCESS_TYPE_MAILLINK";
    public static final String ACCESS_TYPE_SYSTEM = "LOG_ACCESS_TYPE_SYSTEM";

	// 사용자 관리 목록 조회
	public void saveUserListLog(String accessType, String targetId, 
                                String deptCd, String userNm, String userEmail) {
        try {
            String loginUserId = memberUtil.getUserId();

            // 동적으로 조회 조건 생성
            String accessCond = buildAccessCondition(deptCd, userNm, userEmail);
            String remark = buildRemarkQuery(deptCd, userNm, userEmail);

            UserLogDto logEntity = UserLogDto.builder()
                    .userId(loginUserId)
                    .accessUserId(targetId)
                    .accessType(accessType)
                    .accessCond(accessCond)
                    .remark(remark)
                    .accessDt(LocalDateTime.now())
                    .build();

            userLogDao.insert(logEntity);
            
            log.info("사용자 목록 조회 이력 저장 - 사용자: {}, 대상: {}, 조건: {}", 
                     loginUserId, targetId, accessCond);
                     
        } catch (Exception e) {
            log.error("사용자 목록 조회 이력 저장 실패", e);
        }
    }

	public void saveUserDetailLog(String accessType,
                                   String targetUserId, String exposedFields) {
        try {
			String loginUserId = memberUtil.getUserId();
			
            String remark = String.format("SELECT * FROM TC_USER WHERE USER_ID = '%s'", targetUserId);

            UserLogDto logEntity = UserLogDto.builder()
                    .userId(loginUserId)
                    .accessUserId(targetUserId)
                    .accessType(accessType)
                    .accessCond(exposedFields)
                    .remark(remark)
                    .accessDt(LocalDateTime.now())
                    .build();

            userLogDao.insert(logEntity);
            
            log.info("사용자 상세 조회 이력 저장 - 접속자: {}, 대상: {}", loginUserId, targetUserId);
            
        } catch (Exception e) {
            log.error("사용자 상세 조회 이력 저장 실패", e);
        }
    }

	private String buildAccessCondition(String deptCd, String userNm, String userEmail) {
        List<String> conditions = new ArrayList<>();
        
        if (deptCd != null && !deptCd.trim().isEmpty()) {
            conditions.add("deptCd=" + deptCd);
        }
        if (userNm != null && !userNm.trim().isEmpty()) {
            conditions.add("userNm=" + userNm);
        }
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            conditions.add("userEmail=" + userEmail);
        }
        
        return conditions.isEmpty() ? null : String.join(", ", conditions);
    }

	private String buildRemarkQuery(String deptCd, String userNm, String userEmail) {
        List<String> whereClauses = new ArrayList<>();
        
        if (deptCd != null && !deptCd.trim().isEmpty()) {
            whereClauses.add(String.format("DEPT_CD LIKE '%%%s%%'", deptCd));
        }
        if (userNm != null && !userNm.trim().isEmpty()) {
            whereClauses.add(String.format("USER_NM LIKE '%%%s%%'", userNm));
        }
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            whereClauses.add(String.format("USER_EMAIL LIKE '%%%s%%'", userEmail));
        }
        
        if (whereClauses.isEmpty()) {
            return "SELECT * FROM TC_USER";  // 전체 조회
        }
        
        return "SELECT * FROM TC_USER WHERE " + String.join(" AND ", whereClauses);
    }
}

