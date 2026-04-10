package com.hnix.sd.work.software.history;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.software.history.dto.HistorySearchDto;
import com.hnix.sd.work.software.history.dto.HistoryStoreDto;
import com.hnix.sd.work.software.history.dto.HistoryUpdateDto;
import com.hnix.sd.work.software.history.service.SoftwareHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Maintenance History Controller", description = "유지보수 이력 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/software/maintenance")
@RestController
public class SoftwareHistoryController {

    private final SoftwareHistoryService historyService;
    private final ComResponseUtil comResponseUtil;

    @Operation(summary = "유지보수 이력 저장")
    @PostMapping("/store")
    public ComResponseDto<?> storeMaintenanceHistory(@RequestBody HistoryStoreDto storeDto) {
        return comResponseUtil.setResponse200ok( historyService.storeMaintenanceHistory(storeDto) );
    }

    @Operation(summary = "유지보수 이력 조회 (Pagination)")
    @PostMapping("/page")
    public ComResponseDto<?> getSoftwareHistoryPagination(@RequestBody HistorySearchDto searchDto) {
        return comResponseUtil.setResponse200ok( historyService.getSoftwareHistoryByPagination(searchDto) );
    }

    @Operation(summary = "유지보수 이력 조회 (contractNo)")
    @GetMapping("/{contractNo}/list")
    public ComResponseDto<?> getSoftwareHistoryFromContractNo(@PathVariable String contractNo) {
        return comResponseUtil.setResponse200ok( historyService.getSoftwareHistoryFromContractNo(contractNo) );
    }

    @Operation(summary = "유지보수 이력 수정")
    @PostMapping("/update")
    public ComResponseDto<?> updateMaintenanceHistory(@RequestBody HistoryUpdateDto updateDto) {
        historyService.updateMaintenanceHistory(updateDto);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "유지보수 이력 삭제")
    @DeleteMapping("/{historyId}/delete")
    public ComResponseDto<?> updateMaintenanceHistory(@PathVariable String historyId) {
        historyService.removeHistoryInfoById(historyId);
        return comResponseUtil.setResponse200ok();
    }


    @GetMapping("/{appId}")
    public ComResponseDto<?> getSoftwareHistoryFromId(@PathVariable("appId") String id) {
        return comResponseUtil.setResponse200ok( historyService.getSoftwareHistoryFromId(id) );
    }

}
