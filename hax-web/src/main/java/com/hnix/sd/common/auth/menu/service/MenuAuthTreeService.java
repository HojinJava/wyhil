package com.hnix.sd.common.auth.menu.service;

import com.hnix.sd.common.auth.menu.dao.MenuAuthDao;
import com.hnix.sd.common.auth.menu.dto.LoginUserMenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuAuthTreeService {

    private final MenuAuthDao menuAuthDao;

    public List<LoginUserMenuDto> loginUserMenuAccess(final String userId) {
        return menuAuthDao.getLoginUserMenuAccess(userId);
    }

}
