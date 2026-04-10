package com.hnix.sd.common.auth.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuAuthInfoDto {

    private String groupCd;
    private String menuCd;
    private String menuNm;
    private String prntMenuCd;
    private Character authCreateYn;
    private Character authDeleteYn;
    private Character authReadYn;
    private Character authUpdateYn;
    private String menuLink;
    private Integer menuSort;

}
