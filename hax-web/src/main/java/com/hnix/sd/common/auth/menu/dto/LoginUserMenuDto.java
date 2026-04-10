package com.hnix.sd.common.auth.menu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginUserMenuDto {

    private String menuCd;
    private String menuNm;
    private String menuLink;
    private Character authCreateYn;
    private Character authDeleteYn;
    private Character authReadYn;
    private Character authUpdateYn;
    private String menuDesc;
    private Integer menuLevel;
    private Integer menuSort;
    private String prntMenuCd;
    private String useYn;
}
