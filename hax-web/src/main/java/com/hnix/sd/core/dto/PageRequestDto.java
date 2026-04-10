package com.hnix.sd.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {

    private int page;
    private int size;
    private String sort;
    private String direction;

    @Builder.Default
    private int offset = 0;

    @Builder.Default
    private int limit = 10;

    public void calculateOffset() {
        this.page = Math.max(0, this.page);
        this.size = Math.max(1, this.size);
        this.offset = this.page * this.size;
        this.limit = this.size;
    }
}
