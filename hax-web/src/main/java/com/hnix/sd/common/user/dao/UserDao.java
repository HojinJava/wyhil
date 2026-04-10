package com.hnix.sd.common.user.dao;

import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.dto.UserDetailDto;
import com.hnix.sd.common.department.dto.DepartmentStructureDto;
import com.hnix.sd.work.registration.record.dto.RequesterUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDateTime;

@Mapper
public interface UserDao {

    UserDto findByUserIdEntity(@Param("userId") String userId);

    default Optional<UserDto> findById(String userId) {
        return Optional.ofNullable(findByUserIdEntity(userId));
    }

    default Optional<UserDto> findByUserId(String userId) {
        return Optional.ofNullable(findByUserIdEntity(userId));
    }

    UserDto findByUserEmailEntity(@Param("userEmail") String userEmail);

    default Optional<UserDto> findByUserEmail(String userEmail) {
        return Optional.ofNullable(findByUserEmailEntity(userEmail));
    }

    UserDto findByUserCheckCdAndDeleteYnEntity(@Param("userCheckCd") String userCheckCd, @Param("deleteYn") char deleteYn);

    default Optional<UserDto> findByUserCheckCdAndDeleteYn(String userCheckCd, char deleteYn) {
        return Optional.ofNullable(findByUserCheckCdAndDeleteYnEntity(userCheckCd, deleteYn));
    }

    Set<String> findByUserIdStartingWith(@Param("userIdPrefix") String userIdPrefix);

    boolean existsByUserEmail(@Param("userEmail") String userEmail);

    boolean existsByUserEmailAndDeleteYn(@Param("userEmail") String userEmail, @Param("deleteYn") char deleteYn);

    void insertUser(UserDto user);

    List<UserDto> findAll();

    void saveAll(@Param("users") List<UserDto> users);

    boolean existsByUserId(@Param("userId") String userId);

    void updateUser(UserDto user);

    default UserDto save(UserDto user) {
        if (findByUserIdEntity(user.getUserId()) == null) {
            insertUser(user);
        } else {
            updateUser(user);
        }
        return user;
    }

    void deleteById(@Param("userId") String userId);

    void deleteByIds(@Param("userIds") List<String> userIds);

    List<Map<String, Object>> findAllDeptAndUser();

    Map<String, Object> findByUserWithDeptEntity(@Param("userId") String userId);

    default Optional<Map<String, Object>> findByUserWithDept(String userId) {
        return Optional.ofNullable(findByUserWithDeptEntity(userId));
    }

    String findCompanyNameByDeptCd(@Param("deptCd") String deptCd);

    String findDepartmentNameByDeptCd(@Param("deptCd") String deptCd);

    List<String> findMgrCompCodesByUserId(@Param("userId") String userId);

    String findMgrCompNamesByUserId(@Param("userId") String userId);

    void deleteMgrCompByUserId(@Param("userId") String userId);

    void saveMgrComp(@Param("userId") String userId, @Param("compCd") String compCd, @Param("regId") String regId, @Param("regDt") LocalDateTime regDt);

    List<Map<String, Object>> getAuthGroupUser(@Param("userId") String userId);

    List<Map<String, Object>> getRequesterByEmail(@Param("userEmail") String userEmail);

    List<RequesterUserDto> getRequesterByDeptCd(@Param("deptCode") String deptCode, @Param("isRequester") String isRequester);

    int updateUserExpireConsentByUserId(@Param("userId") String userId);

    List<com.hnix.sd.common.user.dto.UserGridDto> findUserGridPagination(com.hnix.sd.common.user.dto.UserSearchPageDto searchDto);

    long countUserGridPagination(com.hnix.sd.common.user.dto.UserSearchPageDto searchDto);

    List<com.hnix.sd.common.user.dto.UserGridDto> findAllUserGrid(com.hnix.sd.common.user.dto.UserSearchPageDto searchDto);

    int insertCommonHistoryForConsent (Map<String, String> map);

    default UserDto from(UserDto u, com.hnix.sd.common.department.dto.DepartmentDto d, String company) {
        if (d != null) u.setDeptNm(d.getDeptNm());
        u.setCompany(company);
        return u;
    }

    default UserDetailDto from(UserDto u, DepartmentStructureDto dept) {
        if (u == null) return null;
        UserDetailDto dto = new UserDetailDto();
        dto.setUserId(u.getUserId());
        dto.setUserNm(u.getUserNm());
        dto.setUserEmail(u.getUserEmail());
        dto.setDeptCd(u.getDeptCd());
        dto.setUserDeptNm(u.getUserDeptNm());
        dto.setUserPositionNm(u.getUserPositionNm());
        dto.setUserPhoneOffice(u.getUserPhoneOffice());
        dto.setUserPhoneMobile(u.getUserPhoneMobile());
        dto.setRemark(u.getRemark());
        if (dept != null) {
            dto.setCompanyNm(dept.getCompanyNm());
            dto.setDepartmentNm(dept.getDepartmentNm());
        }
        return dto;
    }

    default UserDto mapToUserDto(Map<String, Object> map) {
        if (map == null) return null;
        UserDto dto = new UserDto();
        dto.setUserId(getStr(map, "userId", "USER_ID"));
        dto.setUserNm(getStrNonNull(map, "userNm", "USER_NM"));
        dto.setUserEmail(getStr(map, "userEmail", "USER_EMAIL"));
        dto.setDeptCd(getStr(map, "deptCd", "DEPT_CD"));
        dto.setUserDeptNm(getStr(map, "userDeptNm", "USER_DEPT_NM"));
        dto.setUserPositionNm(getStr(map, "userPositionNm", "USER_POSITION_NM"));
        dto.setUserPhoneOffice(getStr(map, "userPhoneOffice", "USER_PHONE_OFFICE"));
        dto.setUserPhoneMobile(getStr(map, "userPhoneMobile", "USER_PHONE_MOBILE"));
        dto.setRemark(getStr(map, "remark", "REMARK"));
        dto.setDeleteYn(getChar(map, "deleteYn", "DELETE_YN"));
        dto.setUserCertYn(getChar(map, "userCertYn", "USER_CERT_YN"));
        dto.setUserConsentYn(getChar(map, "userConsentYn", "USER_CONSENT_YN"));
        dto.setRegId(getStr(map, "regId", "REG_ID"));
        dto.setModId(getStr(map, "modId", "MOD_ID"));
        dto.setRegDt((LocalDateTime) (map.get("regDt") != null ? map.get("regDt") : map.get("REG_DT")));
        dto.setModDt((LocalDateTime) (map.get("modDt") != null ? map.get("modDt") : map.get("MOD_DT")));
        return dto;
    }

    private static String getStr(Map<String, Object> map, String camel, String snake) {
        Object v = map.get(camel);
        if (v == null) v = map.get(snake);
        return v != null ? v.toString() : null;
    }

    private static String getStrNonNull(Map<String, Object> map, String camel, String snake) {
        String v = getStr(map, camel, snake);
        return v != null ? v : "";
    }

    private static Character getChar(Map<String, Object> map, String camel, String snake) {
        Object v = map.get(camel);
        if (v == null) v = map.get(snake);
        if (v == null) return null;
        if (v instanceof Character) return (Character) v;
        String s = v.toString();
        return s.isEmpty() ? null : s.charAt(0);
    }

}
