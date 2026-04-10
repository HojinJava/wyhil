package com.hnix.sd.common.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupCd;
    private String codeCd;

    private Integer codeSeq;

    private Character useYn;

    private String codeText;

    private String codeText2;

    private String codeText3;

    private String codeText4;

    private String codeText5;

    private String codeVal;

    private String codeVal2;

    private String codeVal3;

    private String codeVal4;

    private String codeVal5;

    private String codeDesc;

    private String regId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;

    private String modId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;

    public Character getUseYn() {
        return useYn != null ? useYn : 'Y';
    }
}
