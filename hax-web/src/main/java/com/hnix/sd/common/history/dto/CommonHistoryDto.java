package com.hnix.sd.common.history.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonHistoryDto implements Serializable {

    private String targetId;
    private String menuCd;
    private String hisTypeCd;
    private String hisContents;
    private String userId;
    private String fileId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime hisDt;

    private java.util.List<com.hnix.sd.common.file.dto.FileDto> fileList;

}
