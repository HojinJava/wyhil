package com.hnix.sd.work.software.partner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subCode;
    private String swCode;
    private String compCode;
    private String partnerTypeCd;
    private String partnerContractCd;
    private String contractCancelPossibleYn;
    private String upgradePossibleYn;
    private String technicalSupportYn;
    private String visitSupportYn;
    private String regularServiceCd;
    private String subscriptionYn;
    private String remark;
    private String regId;
    private String modId;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;

}
