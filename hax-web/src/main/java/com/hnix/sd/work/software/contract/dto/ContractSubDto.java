package com.hnix.sd.work.software.contract.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractSubDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String contractNo;
    private String mgrDeptNm;
    private String mgrDeptCompNm;
    private String mgrDeptBizNm;
    private String swNm;
    private String partnerCompCd;
    private String partnerCompNm;

}
