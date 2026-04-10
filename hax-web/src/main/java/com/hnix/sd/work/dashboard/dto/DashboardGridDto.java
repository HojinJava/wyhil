package com.hnix.sd.work.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardGridDto {
  private String deptNm;
  private Integer sendCount;
  private Integer readCount;
  private double readRate;
  private double avgSat;
}
