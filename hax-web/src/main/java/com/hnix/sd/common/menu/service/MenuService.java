package com.hnix.sd.common.menu.service;

import com.hnix.sd.common.menu.dto.FailedMenuDto;
import com.hnix.sd.common.menu.dto.RemoveMenuDto;
import com.hnix.sd.common.menu.dto.MenuDto;
import com.hnix.sd.common.menu.dto.MenuInfoDto;
import com.hnix.sd.common.menu.dao.MenuDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuDao menuDao;

    public List<MenuDto> getTreeDataFromMenu() {
        return menuDao.findAllByOrderByMenuLevelAscMenuSortAsc();
    }

    public MenuDto getMenuInfoById(String menuCd) {
        return menuDao.findById(menuCd).orElseGet(MenuDto::new);
    }

    public List<FailedMenuDto> updateMultipleMenuDto(List<MenuDto> menuUpdateList) {
        List<FailedMenuDto> failedUpdated = new ArrayList<>();

        for (var menu : menuUpdateList) {
            if ( !StringUtils.isEmpty(menu.getMenuCd()) ) {
                continue;
            }
            failedUpdated.add(new FailedMenuDto(menu.getMenuCd(), menu.getMenuNm()));
        }

        menuDao.saveAll(menuUpdateList);
        return failedUpdated;
    }

    public List<FailedMenuDto> updateMultipleMenu(List<MenuInfoDto> menuUpdateList) {
        List<MenuDto> menuDtoList = menuUpdateList.stream()
                .map(this::convertToMenuDto)
                .toList();
        return updateMultipleMenuDto(menuDtoList);
    }

    private MenuDto convertToMenuDto(MenuInfoDto menuInfoDto) {
        MenuDto menuDto = new MenuDto();
        menuDto.setMenuCd(menuInfoDto.getMenuCd());
        menuDto.setMenuNm(menuInfoDto.getMenuNm());
        menuDto.setMenuDesc(menuInfoDto.getMenuDesc());
        menuDto.setMenuLink(menuInfoDto.getMenuLink());
        menuDto.setMenuLevel(menuInfoDto.getMenuLevel());
        menuDto.setMenuSort(menuInfoDto.getMenuSort());
        menuDto.setPrntMenuCd(menuInfoDto.getPrntMenuCd());
        menuDto.setUseYn(menuInfoDto.getUseYn());
        return menuDto;
    }

    public void storeMenuInfo(MenuDto requestDto) {
        MenuDto menu = menuDao.findById(requestDto.getMenuCd()).orElseGet(MenuDto::new);

        if ( StringUtils.isEmpty(menu.getMenuCd()) ) {
            menu.setMenuCd(requestDto.getMenuCd());
            menu.setRegDt(LocalDateTime.now());
            menu.setRegId("admin");
        }

        menu.setModDt(LocalDateTime.now());
        menu.setModId("admin");

        menu.setMenuNm(requestDto.getMenuNm());
        menu.setMenuDesc(requestDto.getMenuDesc());
        menu.setMenuLink(requestDto.getMenuLink());
        menu.setMenuLevel(requestDto.getMenuLevel());
        menu.setMenuSort(requestDto.getMenuSort());
        menu.setUseYn(requestDto.getUseYn());

        menuDao.save(menu);
    }

    public Set<String> checkDuplicateCodes(List<String> menuCodes) {
        return menuDao.findExistingCodes(menuCodes);
    }


    @Transactional
    public void removeMenus(RemoveMenuDto menu) {
        menuDao.deleteByMenuCds(menu.getMenuCds());
    }

}
