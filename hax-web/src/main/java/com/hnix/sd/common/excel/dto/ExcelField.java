package com.hnix.sd.common.excel.dto;

import com.hnix.sd.common.excel.util.ExcelFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExcelField {

    private String field;
    private String title;
    private ExcelFormat.ALIGN align;
    private int width;

}
