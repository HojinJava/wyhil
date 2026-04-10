package com.hnix.sd.core.utils;

import com.hnix.sd.core.dto.PageRequestDto;

public class PageableUtil {

    public static PageRequestDto createPageRequest(int page, int size, String sortBy) {
        PageRequestDto requestDto = new PageRequestDto();
        requestDto.setPage(page);
        requestDto.setSize(size);
        
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String[] sortFields = sortBy.split(",");
            if (sortFields.length > 0) {
                // For simplicity, take the first sort field
                String[] sortField = sortFields[0].trim().split(" ");
                requestDto.setSort(sortField[0]);
                if (sortField.length == 2 && sortField[1].equalsIgnoreCase("desc")) {
                    requestDto.setDirection("DESC");
                } else {
                    requestDto.setDirection("ASC");
                }
            }
        }
        
        requestDto.calculateOffset();
        return requestDto;
    }

}
