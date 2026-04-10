package com.hnix.sd.common.auth.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuAuthInfoDto {

    private String menuCd;
    private String menuNm;
    private String groupCd;
    private Character authCreateYn;
    private Character authUpdateYn;
    private Character authReadYn;
    private Character authDeleteYn;
    private String regId;
    private String modId;

}
