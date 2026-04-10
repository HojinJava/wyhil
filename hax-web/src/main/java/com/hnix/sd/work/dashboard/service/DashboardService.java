package com.hnix.sd.work.dashboard.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.hnix.sd.work.dashboard.dao.DashboardDao;
import com.hnix.sd.work.dashboard.dto.DashboardChartDto;
import com.hnix.sd.work.dashboard.dto.DashboardDto;
import com.hnix.sd.work.dashboard.dto.DashboardGridDto;

@RequiredArgsConstructor
@Service
public class DashboardService {
  private final DashboardDao dashboardDao;

  public DashboardDto getDashboardList(DashboardDto dashboardDto) {
    List<Object[]> dashboardList = dashboardDao.findByCompCdAndYearAndMonth(dashboardDto);
    List<Object[]> companyList = dashboardDao.findByCompanyCd();
    Map<String, Integer> monthResultMap = new HashMap<>();
    Map<String, Integer> periodicCountMap = new HashMap<>();
    Map<String, String> companyRegularServiceMap = new HashMap<>();

    // 회사별 그리드 데이터 저장용 Map
    Map<String, Integer> companySendCountMap = new HashMap<>();
    Map<String, Integer> companyReadCountMap = new HashMap<>();
    Map<String, Double> companyPointSumMap = new HashMap<>();

    DashboardChartDto dashboardChartDto = new DashboardChartDto();
    DashboardGridDto dashboardGridDto = new DashboardGridDto();

    Map<String, String> companyCodeToNameMap = new HashMap<>();

    for (Object[] row : companyList) {
      String companyCd = (String) row[0];
      String regularServiceCode = (String) row[1];
      
      periodicCountMap.put(companyCd, 0);
      companyRegularServiceMap.put(companyCd, regularServiceCode);
    }

    int total = dashboardList.size();
    int resCnt = 0;
    double pointSum = 0.0;

    List<DashboardChartDto> pointList = new ArrayList<>();
    pointList.add(new DashboardChartDto("20", "0", "0"));
    pointList.add(new DashboardChartDto("40", "0", "0"));
    pointList.add(new DashboardChartDto("60", "0", "0"));
    pointList.add(new DashboardChartDto("80", "0", "0"));
    pointList.add(new DashboardChartDto("100", "0", "0"));

    int startDate = Integer.parseInt(dashboardDto.getStartDate());
    int endDate = Integer.parseInt(dashboardDto.getEndDate());
    int year = Integer.parseInt(dashboardDto.getSearchYear());
    
    for (Object[] row : dashboardList) {
      String serviceCd = (String) row[11];   // his.SERVICE_CD
      String companyCd = (String) row[6];   // org.company_cd
      String companyNm = (String) row[7];    // org.company_nm
      String regularServiceCode = (String) row[9]; // part.REGULAR_SERVICE_CD

      if (!companyCodeToNameMap.containsKey(companyCd)) {
        companyCodeToNameMap.put(companyCd, companyNm);
      }

      // companyList에서 가져오지 못한 경우를 위한 백업
      if (!companyRegularServiceMap.containsKey(companyCd) && regularServiceCode != null) {
        companyRegularServiceMap.put(companyCd, regularServiceCode);
        periodicCountMap.put(companyCd, 0);
      }

      companySendCountMap.put(companyNm, companySendCountMap.getOrDefault(companyNm, 0) + 1);
      
      Double surveySum = null;
      if (row[8] != null) {
        try {
          if (row[8] instanceof Number) {
            surveySum = ((Number) row[8]).doubleValue();
          } else if (row[8] instanceof String) {
            surveySum = Double.parseDouble((String) row[8]);
          }
        } catch (NumberFormatException e) {
          System.out.println("surveySum 변환 실패: " + row[8]);
        }
      }
      
      Integer day = null;
      Integer month = null;
      if (row[2] != null) {
        if (row[2] instanceof java.sql.Date) {
            java.time.LocalDate localDate = ((java.sql.Date) row[2]).toLocalDate();
            day = localDate.getDayOfMonth();
            month = localDate.getMonthValue();
        } else if (row[2] instanceof java.sql.Timestamp) {
            java.time.LocalDateTime localDateTime = ((java.sql.Timestamp) row[2]).toLocalDateTime();
            day = localDateTime.getDayOfMonth();
            month = localDateTime.getMonthValue();
        }
      }
    
      if (surveySum != null && day != null && month != null) {
          String dateKey = String.format("%d-%d", month, day);
          monthResultMap.put(dateKey, monthResultMap.getOrDefault(dateKey, 0) + 1);
      }
      
      if (surveySum != null && surveySum > 0) {
        resCnt++;
        pointSum += surveySum;

        companyReadCountMap.put(companyNm, companyReadCountMap.getOrDefault(companyNm, 0) + 1);
        companyPointSumMap.put(companyNm, companyPointSumMap.getOrDefault(companyNm, 0.0) + surveySum);

        if(surveySum >= 20 && surveySum < 40) {
          int currentValue = Integer.parseInt(pointList.get(0).getYValue());
          pointList.get(0).setYValue(String.valueOf(currentValue + 1));
        }
        if(surveySum >= 40 && surveySum < 60) {
          int currentValue = Integer.parseInt(pointList.get(1).getYValue());
          pointList.get(1).setYValue(String.valueOf(currentValue + 1));
        }
        if(surveySum >= 60 && surveySum < 80) {
          int currentValue = Integer.parseInt(pointList.get(2).getYValue());
          pointList.get(2).setYValue(String.valueOf(currentValue + 1));
        }
        if(surveySum >= 80 && surveySum < 100) {
          int currentValue = Integer.parseInt(pointList.get(3).getYValue());
          pointList.get(3).setYValue(String.valueOf(currentValue + 1));
        }
        if(surveySum == 100) {
          int currentValue = Integer.parseInt(pointList.get(4).getYValue());
          pointList.get(4).setYValue(String.valueOf(currentValue + 1));
        }
      }

      // 정기점검 실적 카운트 (회사코드 기준)
      if ("SERVICE_ITEM_PERIODIC".equals(serviceCd)) {
        periodicCountMap.put(companyCd, periodicCountMap.getOrDefault(companyCd, 0) + 1);
      }
    }

    List<DashboardChartDto> planList = new ArrayList<>();
    int totalPlanCount = 0;

    Set<String> addedCompanies = new HashSet<>();
    
    for (Map.Entry<String, String> entry : companyRegularServiceMap.entrySet()) {
        String companyCd = entry.getKey();
        String regularServiceCode = entry.getValue();
        
        String companyNm = companyCodeToNameMap.getOrDefault(companyCd, companyCd);

        if (addedCompanies.contains(companyNm)) {
          continue;
        }
        
        // 특정 고객사 필터링
        if (dashboardDto.getCustomerCode() != null && 
            !dashboardDto.getCustomerCode().isEmpty() && 
            !"%%".equals(dashboardDto.getCustomerCode()) &&
            !dashboardDto.getCustomerCode().equals(companyCd)) {
            continue;
        }
        
        int planCount = calculatePlanCount(startDate, endDate, regularServiceCode);
        totalPlanCount += planCount;
        
        int actualCount = periodicCountMap.getOrDefault(companyCd, 0);
        
        DashboardChartDto chartData = new DashboardChartDto(
            companyNm,
            String.valueOf(actualCount),  // yValue: 실적
            String.valueOf(planCount)     // ySubValue: 계획
        );
        planList.add(chartData);
        addedCompanies.add(companyNm);
    }

    List<DashboardChartDto> responseTrendList = new ArrayList<>();
    int cumulativeCount = 0;
    int periodMonths = endDate - startDate + 1;

    if (periodMonths == 1) {
      // 월별 조회 - 일별 표시
      YearMonth yearMonth = YearMonth.of(year, startDate);
      int lastDayOfMonth = yearMonth.lengthOfMonth();
      
      for (int day = 1; day <= lastDayOfMonth; day++) {
        String dateKey = startDate + "-" + day;
        int dailyCount = monthResultMap.getOrDefault(dateKey, 0);
        cumulativeCount += dailyCount;
        
        responseTrendList.add(new DashboardChartDto(
            day + "일",
            String.valueOf(cumulativeCount),
            String.valueOf(dailyCount)
        ));
      }
    } else if (periodMonths == 12) {
      // 년도별 조회 - 월별 표시
      Map<Integer, Integer> monthlyResultMap = new HashMap<>();
      
      for (Map.Entry<String, Integer> entry : monthResultMap.entrySet()) {
        String[] parts = entry.getKey().split("-");
        int month = Integer.parseInt(parts[0]);
        monthlyResultMap.put(month, monthlyResultMap.getOrDefault(month, 0) + entry.getValue());
      }
      
      for (int month = 1; month <= 12; month++) {
        int monthlyCount = monthlyResultMap.getOrDefault(month, 0);
        cumulativeCount += monthlyCount;
        
        responseTrendList.add(new DashboardChartDto(
          month + "월",
          String.valueOf(cumulativeCount),
          String.valueOf(monthlyCount)
        ));
      }
    } else {
      // 분기/반기 조회 - 일별 표시 (월 포함)
      for (int month = startDate; month <= endDate; month++) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int lastDayOfMonth = yearMonth.lengthOfMonth();
        
        for (int day = 1; day <= lastDayOfMonth; day++) {
          String dateKey = month + "-" + day;
          int dailyCount = monthResultMap.getOrDefault(dateKey, 0);
          cumulativeCount += dailyCount;
          
          responseTrendList.add(new DashboardChartDto(
              month + "월 " + day + "일",
              String.valueOf(cumulativeCount),
              String.valueOf(dailyCount)
          ));
        }
      }
    }

    double resRate = total > 0 ? Math.round((double) resCnt / total * 100 * 10.0) / 10.0 : 0.0;
    dashboardDto.setResponseRate(resRate);

    double averagePoint = total > 0 ? Math.round(pointSum / resCnt * 10.0) / 10.0 : 0.0;
    dashboardDto.setAvgSat(averagePoint);

    List<DashboardGridDto> gridList = new ArrayList<>();
    Set<String> addedGridCompanies = new HashSet<>();
    
    for (Map.Entry<String, String> entry : companyRegularServiceMap.entrySet()) {
      String companyCd = entry.getKey();
      String regularServiceCode = entry.getValue();

      String companyNm = companyCodeToNameMap.getOrDefault(companyCd, companyCd);
        
      // 이미 추가된 회사명이면 스킵
      if (addedGridCompanies.contains(companyNm)) {
          continue;
      }
        
      // 특정 고객사 필터링
      if (dashboardDto.getCustomerCode() != null && 
          !dashboardDto.getCustomerCode().isEmpty() && 
          !"%%".equals(dashboardDto.getCustomerCode()) &&
          !dashboardDto.getCustomerCode().equals(companyCd)) {
          continue;
      }
        
      gridList.add(new DashboardGridDto(companyNm, 0, 0, 0.0, 0.0));
      addedGridCompanies.add(companyNm);
    }


    // 회사별로 gridList 업데이트
    for (DashboardGridDto dto : gridList) {
      String companyNm = dto.getDeptNm();
      
      int sendCount = companySendCountMap.getOrDefault(companyNm, 0);
      int readCount = companyReadCountMap.getOrDefault(companyNm, 0);
      double pointSumCompany = companyPointSumMap.getOrDefault(companyNm, 0.0);
      
      double readRate = sendCount > 0 ? Math.round((double) readCount / sendCount * 100 * 10.0) / 10.0 : 0.0;
      double satAvg = readCount > 0 ? Math.round(pointSumCompany / readCount * 10.0) / 10.0 : 0.0;
      
      dto.setSendCount(sendCount);
      dto.setReadCount(readCount);
      dto.setReadRate(readRate);
      dto.setAvgSat(satAvg);
    }

    dashboardDto.setAvgSatChart(pointList);
    

    int totalPeriodic = periodicCountMap.values().stream().mapToInt(Integer::intValue).sum();
    double planRate = totalPlanCount > 0 ? Math.round(((double) totalPeriodic / totalPlanCount * 100) * 10) / 10.0 : 0;

    dashboardDto.setResultToPlanChart(planList);
    dashboardDto.setResultToPlan(planRate);
    dashboardDto.setResAll(totalPeriodic);
    dashboardDto.setPlanAll(totalPlanCount);
    dashboardDto.setResponseRateChart(responseTrendList);
    dashboardDto.setGridList(gridList);

    return dashboardDto;
  }

  private int calculatePlanCount(int startDate, int endDate, String regularServiceCode) {
    int monthRange = endDate - startDate + 1;
    
    if (startDate == 1 && endDate == 12) { // 1년 전체
      if ("REGULAR_SERVICE_MONTH".equals(regularServiceCode)) return 12;
      if ("REGULAR_SERVICE_QUARTER".equals(regularServiceCode)) return 4;
      if ("REGULAR_SERVICE_HALF".equals(regularServiceCode)) return 2;
      if ("REGULAR_SERVICE_YEAR".equals(regularServiceCode)) return 1;
    } else if ((startDate == 1 && endDate == 6) || (startDate == 7 && endDate == 12)) { // 반기
      if ("REGULAR_SERVICE_MONTH".equals(regularServiceCode)) return 6;
      if ("REGULAR_SERVICE_QUARTER".equals(regularServiceCode)) return 2;
      if ("REGULAR_SERVICE_HALF".equals(regularServiceCode)) return 1;
      if ("REGULAR_SERVICE_YEAR".equals(regularServiceCode)) return 0;
    } else if ((startDate == 1 && endDate == 3) || (startDate == 4 && endDate == 6) || (startDate == 7 && endDate == 9) || (startDate == 10 && endDate == 12)) { // 분기
      if ("REGULAR_SERVICE_MONTH".equals(regularServiceCode)) return 3;
      if ("REGULAR_SERVICE_QUARTER".equals(regularServiceCode)) return 1;
      if ("REGULAR_SERVICE_HALF".equals(regularServiceCode)) return 0;
      if ("REGULAR_SERVICE_YEAR".equals(regularServiceCode)) return 0;
    } else if (monthRange == 1) { // 1개월
      if ("REGULAR_SERVICE_MONTH".equals(regularServiceCode)) return 1;
      return 0;
    }
    return 0;
  }
}
