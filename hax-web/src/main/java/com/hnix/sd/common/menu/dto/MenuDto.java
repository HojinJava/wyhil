package com.hnix.sd.common.menu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "메뉴 코드는 필수 입력 값입니다.")
    private String menuCd;

    @NotNull
    private String menuNm;

    private String menuDesc;

    private String menuLink;

    private Integer menuLevel;

    private Integer menuSort;

    private String prntMenuCd;

    private Character useYn;

    private String regId;

    private String modId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;

    public Character getUseYn() { return useYn != null ? useYn : 'Y'; }

}
