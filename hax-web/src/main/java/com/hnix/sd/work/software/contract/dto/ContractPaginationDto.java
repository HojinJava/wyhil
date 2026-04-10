package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContractPaginationDto {

    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Long totalElements;
    private List<ContractGridDto> elements;

}
