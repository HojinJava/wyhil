package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkRegisterPaginationDto {

    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Long totalElements;
    private List<WorkRegisterGridDto> elements;

}
