package com.hnix.sd.common.user.dao;

import com.hnix.sd.common.user.dto.UserLogDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserLogMapper {

    void insert(UserLogDto userLog);

    List<UserLogDto> findAll();

    List<UserLogDto> findByUserId(String userId);
}
