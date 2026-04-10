package com.hnix.sd.work.software.history.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hnix.sd.common.file.dto.FileDto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareHistoryDto {

    private Long hisNo;
    private String contractNo;
    private LocalDateTime hisDt;
    private String historyTypeCd;
    private Integer historySeq;
    private String historyContents;
    private String remark;
    private String fileId;
    private String regId;
    private Character useYn;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;

    private String modId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;

    private List<FileDto> fileList;

    public List<FileDto> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileDto> fileList) {
        this.fileList = fileList;
    }
}
