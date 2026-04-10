package com.hnix.sd.common.menu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuInfoDto {

    private String menuCd;
    private String menuNm;
    private String menuLink;
    private String menuDesc;
    private Integer menuLevel;
    private Integer menuSort;
    private String prntMenuCd;
    private Character useYn;

}
