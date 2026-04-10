package com.hnix.sd.common.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCodeDto {

    private String codeGroupCd;
    private String codeGroupName;
    private String codeCd;
    private String codeDesc;
    private Character useYn;

}
