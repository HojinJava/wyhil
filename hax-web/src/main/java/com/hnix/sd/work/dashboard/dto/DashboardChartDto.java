package com.hnix.sd.work.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardChartDto {
  private String xValue;
  private String yValue;
  private String ySubValue;
}
