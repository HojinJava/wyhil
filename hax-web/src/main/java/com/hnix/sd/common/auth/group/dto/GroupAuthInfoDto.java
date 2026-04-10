package com.hnix.sd.common.auth.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupAuthInfoDto {

    private String groupCd;
    private String groupNm;
    private String groupDesc;
    private String regId;
    private String modId;

}
