package com.hnix.sd.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;

    public PageResponseDto(List<T> content, PageRequestDto pageRequest, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageSize = pageRequest.getSize() > 0 ? pageRequest.getSize() : 10;
        this.pageNumber = pageRequest.getPage();
        this.totalPages = (int) Math.ceil((double) totalElements / this.pageSize);
    }
}
