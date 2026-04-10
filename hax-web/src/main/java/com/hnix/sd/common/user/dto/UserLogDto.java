package com.hnix.sd.common.user.dto;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String accessUserId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime accessDt;
    private String accessType;
    private String accessCond;
    private String remark;
    
    private String menuCd;
    private Long logId;
    private String logContents;
    private String accessIp;
}
