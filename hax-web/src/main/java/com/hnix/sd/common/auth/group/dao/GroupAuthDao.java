package com.hnix.sd.common.auth.group.dao;

import com.hnix.sd.common.auth.group.dto.GroupAuthDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface GroupAuthDao {

    List<GroupAuthDto> findAll();

    Optional<GroupAuthDto> findByGroupCd(@Param("groupCd") String groupCd);

    void insertGroupAuthDto(GroupAuthDto groupAuth);

    void updateGroupAuthDto(GroupAuthDto groupAuth);

    void deleteByGroupCd(@Param("groupCd") String groupCd);

    boolean existsByGroupCd(@Param("groupCd") String groupCd);

}
