package com.hnix.sd.common.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CodeRegisterDto {

    private String userId;
    private GroupCodeAllDto groupCode;
    private List<SubCodeDto> subCodes;

}
