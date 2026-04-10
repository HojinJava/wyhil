package com.hnix.sd.common.auth.user.dao;

import com.hnix.sd.common.auth.user.dto.AuthUserInfoDto;
import com.hnix.sd.common.auth.user.dto.UserAuthDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAuthDao {

    List<AuthUserInfoDto> findByGroupCdWithDept(@Param("groupCd") String groupCd);

    List<AuthUserInfoDto> findByGroupCdWithUser(@Param("groupCd") String groupCd);

    List<String> findByGroupCdWithUserId(@Param("userId") String userId);

    void deleteByUserIdAndGroupCdAndUserTypeCd(@Param("userId") String userId, @Param("groupCd") String groupCd, @Param("userTypeCd") Character userTypeCd);

    void insertUserAuth(UserAuthDto userAuth);

    void insertUserAuthList(@Param("userAuths") List<UserAuthDto> userAuths);

}
