package com.hnix.sd.common.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CodeRemoveDto {

    private String groupCode;
    private List<String> subCodeNames;

}
