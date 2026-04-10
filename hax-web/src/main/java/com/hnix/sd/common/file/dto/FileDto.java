package com.hnix.sd.common.file.dto;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto implements Serializable {

    private static final long serialVersionUID = 1L;
  
	@Schema(title = "File UUID -  자동 생성", example = "") 
	private String fileUuid;
	
	@Schema(title = "파일 구분 - index", example = "")
	private String fileId;
	
	@Schema(title = "파일명", example = "")
	private String fileNm;

    @Schema(title = "파일경로", example = "")
    private String filePath;

	@Schema(title = "확장자", example = "")	
	private String fileExt;

    @Schema(title = "파일크기", example = "")
	private long fileSize;

	@Schema(title = "파일종류", example = "")
	private String fileContType;

    @Schema(title = "삭제여부", example = "")
    private String delYn;

    @Schema(title = "수정ID", example = "")
    private String modId;

    @Schema(title = "수정일", example = "")
    private LocalDateTime modDt;

    @Schema(title = "등록ID", example = "")
    private String regId;

    @Schema(title = "등록일", example = "")
    private LocalDateTime regDt;
}
