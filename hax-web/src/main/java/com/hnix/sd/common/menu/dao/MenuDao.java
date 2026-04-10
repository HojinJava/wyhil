package com.hnix.sd.common.menu.dao;

import com.hnix.sd.common.menu.dto.MenuDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface MenuDao {

    List<MenuDto> findAllByOrderByMenuLevelAscMenuSortAsc();

    MenuDto findByMenuCdEntity(@Param("menuCd") String menuCd);

    default Optional<MenuDto> findById(String menuCd) {
        return Optional.ofNullable(findByMenuCdEntity(menuCd));
    }

    Set<String> findExistingCodes(@Param("menuCodes") List<String> menuCodes);

    void insertMenu(MenuDto menu);

    void updateMenu(MenuDto menu);

    default void save(MenuDto menu) {
        MenuDto existing = findByMenuCdEntity(menu.getMenuCd());
        if (existing == null) {
            insertMenu(menu);
        } else {
            updateMenu(menu);
        }
    }

    default void saveAll(List<MenuDto> menus) {
        for (MenuDto menu : menus) {
            save(menu);
        }
    }

    void deleteByMenuCds(@Param("menuCds") List<String> menuCds);
}
