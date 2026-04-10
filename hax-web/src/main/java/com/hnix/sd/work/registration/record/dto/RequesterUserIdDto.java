package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequesterUserIdDto {

    private String serviceNo;
    private String userId;

    public RequesterUserIdDto(String serviceNo, String userId) {
        this.serviceNo = serviceNo;
        this.userId = userId;
    }
}
