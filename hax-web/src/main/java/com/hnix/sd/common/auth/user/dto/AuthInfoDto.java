package com.hnix.sd.common.auth.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfoDto {

    private String groupCd;
    private String userId;
    private Character userTypeCd;
    private String regId;
    private String modId;

}
