package com.hnix.sd.common.auth.menu.service;

import com.hnix.sd.common.auth.menu.dao.MenuAuthDao;
import com.hnix.sd.common.auth.menu.dto.MenuAuthIdDto;
import com.hnix.sd.common.auth.menu.dto.MenuAuthInfoDto;
import com.hnix.sd.common.auth.menu.dto.UpdateMenuAuthInfoDto;
import com.hnix.sd.common.auth.menu.dto.MenuAuthDto;
import com.hnix.sd.common.menu.dto.MenuDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MenuAuthService {

    private final MenuAuthDao menuAuthDao;


    public List<MenuAuthInfoDto> getMenuAuthListByGroupCd(String groupCd) {
        return menuAuthDao.findAllByGroupCd(groupCd)
                .stream()
                .map(objects -> {
                    MenuAuthDto menuAuth = (MenuAuthDto) objects[0];
                    MenuDto menu = (MenuDto) objects[1];
                    return toDTO(menuAuth, menu);
                })
                .collect(Collectors.toList());
    }

    public List<MenuAuthIdDto> updateMultipleMenuAuth(List<UpdateMenuAuthInfoDto> authInfoDtoList) {
        List<MenuAuthIdDto> failedRegist = new ArrayList<>();
        List<MenuAuthDto> menuAuthEntities = new ArrayList<>();

        for (var menuAuth : authInfoDtoList) {
            if ( !StringUtils.isEmpty(menuAuth.getMenuCd()) &&
                    !StringUtils.isEmpty(menuAuth.getGroupCd())) {
                menuAuthEntities.add(toEntity(menuAuth));
                continue;
            }
            failedRegist.add(new MenuAuthIdDto(menuAuth.getMenuCd(), menuAuth.getGroupCd()));
        }

        // Insert one by one
        for (MenuAuthDto entity : menuAuthEntities) {
            menuAuthDao.insert(entity);
        }

        return failedRegist;
    }

    @Transactional
    public void deleteMultipleMenuAuth(List<MenuAuthIdDto> menuAuthIdDtos) {
        if (menuAuthIdDtos.isEmpty()) return;

        for (var authId : menuAuthIdDtos) {
            String menuCd = authId.getMenuCd();
            String groupCd = authId.getGroupCd();

            if ( !StringUtils.isEmpty(menuCd) &&
                    !StringUtils.isEmpty(groupCd)) {
                menuAuthDao.deleteByMenuCdAndGroupCd(menuCd, groupCd);
            }
        }
    }

    private MenuAuthInfoDto toDTO(MenuAuthDto menuAuth, MenuDto menu) {
        MenuAuthInfoDto dto = new MenuAuthInfoDto();
        dto.setGroupCd(menuAuth.getGroupCd());
        dto.setMenuCd(menuAuth.getMenuCd());
        dto.setMenuNm(menu.getMenuNm());
        dto.setPrntMenuCd(menu.getPrntMenuCd());
        dto.setAuthCreateYn(menuAuth.getAuthCreateYn());
        dto.setAuthDeleteYn(menuAuth.getAuthDeleteYn());
        dto.setAuthReadYn(menuAuth.getAuthReadYn());
        dto.setAuthUpdateYn(menuAuth.getAuthUpdateYn());
        dto.setMenuLink(menu.getMenuLink());
        dto.setMenuSort(menu.getMenuSort());
        return dto;
    }

    private MenuAuthDto toEntity(UpdateMenuAuthInfoDto dto) {
        MenuAuthDto entity = new MenuAuthDto();
        entity.setMenuCd(dto.getMenuCd());
        entity.setGroupCd(dto.getGroupCd());
        entity.setAuthCreateYn(dto.getAuthCreateYn());
        entity.setAuthUpdateYn(dto.getAuthUpdateYn());
        entity.setAuthReadYn(dto.getAuthReadYn());
        entity.setAuthDeleteYn(dto.getAuthDeleteYn());
        entity.setRegId(dto.getRegId());
        entity.setModId(dto.getModId());
        return entity;
    }

}
