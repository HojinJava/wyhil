package com.hnix.sd.work.software.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareStoreResultDto {

    private String swCode;
    private String swName;
    private String result;
    private String resultMsg;

}
