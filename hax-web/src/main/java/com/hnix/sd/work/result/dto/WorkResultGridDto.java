package com.hnix.sd.work.result.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@NoArgsConstructor
public class WorkResultGridDto {

    private String serviceNo;
    private String contractNo;
    private String subCd;
    private String serviceCd;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime reqDt;

    private String statusCd;
    private String reqCompCd;
    private String reqUserId;
    private String reqSupportCd;
    private String reqContents;
    private String reqUserContractType;
    private LocalDateTime procDt;
    private String procUserNm;
    private String procDeptCd;
    private String procSupportCd;
    private String procContents;
    private String procResultCd;
    private String remark;
    private String modId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;


    private String regId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;


    private String swName;
    private String serviceCdNm;
    private String customerCompanyNm;
    private String customerDepartmentNm;
    private String customerManageNm;
    private String reqUserNm;
    private String userDeptNm;
    private String reqUserDepartmentNm;
    private String reqUserCompanyNm;    
    private String partnerCompanyNm;
    private String certComment;
    private Character pointDisYn;
    private Integer pointSum;
	private String certCd;
	private String certCdNm;
	private LocalDateTime certDt;
	private String procSupportNm; //처리매체코드
	private String statusNm;
	private String procResultNm;
    private String fileNames; //파일명
}
