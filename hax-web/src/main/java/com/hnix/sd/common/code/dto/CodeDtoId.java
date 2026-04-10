package com.hnix.sd.common.code.dto;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CodeDtoId implements Serializable {
    private String codeGroupCd;
    private String codeCd;
}
