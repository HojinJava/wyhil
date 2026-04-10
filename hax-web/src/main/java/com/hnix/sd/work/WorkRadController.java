package com.hnix.sd.work;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Work RAD Controller", description = "작업 RAD 마이그레이션 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/work/rad")
@RestController
public class WorkRadController {

    private final WorkRadService workRadService;
    private final ComResponseUtil comResponseUtil;

    @Operation(summary = "작업 목록 조회")
    @PostMapping("/list")
    public ComResponseDto<?> getWorkList(@RequestBody Map<String, Object> params) {
        return comResponseUtil.setResponse200ok(workRadService.getWorkList(params));
    }

    @Operation(summary = "작업 상세 조회")
    @PostMapping("/detail")
    public ComResponseDto<?> getWorkDetail(@RequestBody Map<String, Object> params) {
        return comResponseUtil.setResponse200ok(workRadService.getWorkDetail(params));
    }

    @Operation(summary = "보고서 - 협력사 기준")
    @PostMapping("/report/partner")
    public ComResponseDto<?> getReportPartner(@RequestBody Map<String, Object> params) {
        return comResponseUtil.setResponse200ok(workRadService.getReportPartner(params));
    }

    @Operation(summary = "보고서 - SUBCODE 기준")
    @PostMapping("/report/subcode")
    public ComResponseDto<?> getReportSubcode(@RequestBody Map<String, Object> params) {
        return comResponseUtil.setResponse200ok(workRadService.getReportSubcode(params));
    }

    @Operation(summary = "보고서 - SUBCODE 상세")
    @PostMapping("/report/subcode-detail")
    public ComResponseDto<?> getReportSubcodeDetail(@RequestBody Map<String, Object> params) {
        return comResponseUtil.setResponse200ok(workRadService.getReportSubcodeDetail(params));
    }

    @Operation(summary = "서브코드 조회")
    @PostMapping("/subcode")
    public ComResponseDto<?> getSubCode(@RequestBody Map<String, Object> params) {
        return comResponseUtil.setResponse200ok(workRadService.getSubCode(params));
    }

    @Operation(summary = "공통코드 조회")
    @PostMapping("/common-code")
    public ComResponseDto<?> getCommonCode(@RequestBody Map<String, Object> params) {
        return comResponseUtil.setResponse200ok(workRadService.getCommonCode(params));
    }
}
