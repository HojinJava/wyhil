package com.hnix.sd.common.code.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCodeAllDto {

    private String codeGroupCd;
    private String codeGroupName;
    private String codeCd;
    private String codeText;
    private String codeDesc;
    private Integer codeSeq;
    private Character useYn;
    private String regId;
    private LocalDateTime regDt;
    private String modId;
    private LocalDateTime modDt;

}
