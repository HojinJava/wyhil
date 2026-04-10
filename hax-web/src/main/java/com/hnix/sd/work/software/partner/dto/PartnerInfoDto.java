package com.hnix.sd.work.software.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerInfoDto {
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
    private LocalDateTime regDt;
    private String modId;
    private LocalDateTime modDt;
    private String fileId;
}
