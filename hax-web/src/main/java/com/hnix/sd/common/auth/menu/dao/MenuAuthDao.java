package com.hnix.sd.common.auth.menu.dao;

import com.hnix.sd.common.auth.menu.dto.LoginUserMenuDto;
import com.hnix.sd.common.auth.menu.dto.MenuAuthDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MenuAuthDao {

    Optional<MenuAuthDto> findByMenuCdAndGroupCd(@Param("menuCd") String menuCd, @Param("groupCd") String groupCd);

    List<Object[]> findAllByGroupCd(@Param("groupCd") String groupCd);

    void deleteByMenuCdAndGroupCd(@Param("menuCd") String menuCd, @Param("groupCd") String groupCd);

    void insert(MenuAuthDto menuAuth);

    List<LoginUserMenuDto> getLoginUserMenuAccess(@Param("userId") String userId);

}
