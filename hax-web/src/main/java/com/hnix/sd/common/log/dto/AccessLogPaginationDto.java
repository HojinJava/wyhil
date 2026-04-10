package com.hnix.sd.common.log.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessLogPaginationDto {

    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Long totalElements;
    private List<AccessLogInfoDto> elements;

}
