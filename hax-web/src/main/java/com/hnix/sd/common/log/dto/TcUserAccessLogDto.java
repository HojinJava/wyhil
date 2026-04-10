package com.hnix.sd.common.log.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcUserAccessLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime accessDt;
    private String userId;
    private String menuCd;
    private String menuType;
    private String userSessionId;
    private String userIp;
}
