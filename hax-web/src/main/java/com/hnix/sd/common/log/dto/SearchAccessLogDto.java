package com.hnix.sd.common.log.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SearchAccessLogDto {

    private Integer pageNo = 0;
    private Integer pageSize = 10;
    private String criteria = "accessDt";
    private String sort = "DESC";
    private LocalDate startDate;
    private LocalDate endDate;

}
