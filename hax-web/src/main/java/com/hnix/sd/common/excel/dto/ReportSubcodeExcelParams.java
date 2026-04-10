package com.hnix.sd.common.excel.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;


@Getter
@Data
@Builder
public class ReportSubcodeExcelParams {
  private String customerCode;
  private String customerCodeText;
  private String contractYear;
  private String partnerContractCd;
  private LocalDate startDate;
  private LocalDate endDate;
}
