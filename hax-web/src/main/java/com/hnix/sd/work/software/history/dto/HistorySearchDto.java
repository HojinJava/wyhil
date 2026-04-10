package com.hnix.sd.work.software.history.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HistorySearchDto {

    private Integer pageNo = 0;
    private Integer pageSize = 10;
    private String sortBy = "";
    private String customer = "";
    private String department = "";
    private String partner = "";
    private String contractYear = "";
    private String contractNo = "";
    private String swName = "";

}
