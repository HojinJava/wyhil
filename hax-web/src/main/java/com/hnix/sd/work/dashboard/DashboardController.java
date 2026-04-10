package com.hnix.sd.work.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.dashboard.dto.DashboardDto;
import com.hnix.sd.work.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;

@RequiredArgsConstructor
@RequestMapping("/work/dashboard")
@RestController
public class DashboardController {
  private final DashboardService dashboardService;
  private final ComResponseUtil comResponseUtil;

  @Operation(summary = "대시보드 조회")
  @PostMapping("/List")
  public ComResponseDto<?> registerMultiContractInfo(@RequestBody DashboardDto dashboardDto) {
    return comResponseUtil.setResponse200ok( dashboardService.getDashboardList(dashboardDto));
  }
}
