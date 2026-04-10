package com.hnix.sd.work.software.history.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryUpdateDto {

    private String historyId;
    private String contractNo;
    private String historyTypeCd;
    private Integer historySeq = 0;
    private String historyContents;
    private String fileId;
    private String modId;

}
