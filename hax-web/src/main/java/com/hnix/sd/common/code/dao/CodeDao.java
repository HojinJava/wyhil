package com.hnix.sd.common.code.dao;

import com.hnix.sd.common.code.dto.CodeDto;
import com.hnix.sd.common.code.dto.GroupCodeAllDto;
import com.hnix.sd.common.code.dto.GroupCodeDto;
import com.hnix.sd.common.code.dto.SubCodeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CodeDao {

    List<GroupCodeAllDto> findAll();

    List<SubCodeDto> findByCodeGroupCd(@Param("groupCodeId") String groupCodeId);

    List<GroupCodeDto> findBySubCodeCd(@Param("codeCd") String codeCd);

    List<SubCodeDto> findByMultiCodeGroupCd(@Param("codeCdIds") List<String> ids);

    Optional<CodeDto> findByCodeId(@Param("codeGroupCd") String codeGroupCd, @Param("codeCd") String codeCd);

    void insert(CodeDto codeDto);

    void update(CodeDto codeDto);

    void deleteByGroupCode(@Param("codeGroupCd") String codeGroupCd);

    void deleteBySubCode(@Param("codeGroupCd") String codeGroupCd,
                         @Param("subCodeCds") List<String> subCodeCds);

}
