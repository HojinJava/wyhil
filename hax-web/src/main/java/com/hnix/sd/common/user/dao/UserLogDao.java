package com.hnix.sd.common.user.dao;

import com.hnix.sd.common.user.dto.UserLogDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserLogDao {
    void insert(UserLogDto userLog);
    List<UserLogDto> findAll();
    List<UserLogDto> findByUserId(@Param("userId") String userId);
}
