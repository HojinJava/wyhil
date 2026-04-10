package com.hnix.sd.work.dashboard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private String surveySum;
    private String serviceNo;
    private String serviceCd;
    private String partnerCode;
    private String customerCode;
    private String periodGubun;
    private String searchPeriod;
    private String searchYear;
    private String startDate;
    private String endDate;

    private double responseRate;
    private double resultToPlan;
    private Integer planAll;
    private Integer resAll;
    private double avgSat;

    private List<DashboardChartDto> responseRateChart;
    private List<DashboardChartDto> resultToPlanChart;
    private List<DashboardChartDto> avgSatChart;
    private List<DashboardGridDto> gridList;
}
