package com.hnix.sd.work.software.manage;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.software.manage.dto.SearchSoftwareDto;
import com.hnix.sd.work.software.manage.dto.SoftwareInfoDto;
import com.hnix.sd.work.software.manage.dto.SoftwareRemoveResultDto;
import com.hnix.sd.work.software.manage.service.SoftwareService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Software Controller", description = "소프트웨어 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/work/software")
@RestController
public class SoftwareController {

    private final SoftwareService softwareService;
    private final ComResponseUtil responseUtil;


    @Operation(summary = "소프트웨어 목록 전체 조회")
    @GetMapping("/all")
    public ComResponseDto<?> getSoftwareListAll() {
        return responseUtil.setResponse200ok(softwareService.getSoftwareListAll());
    }

    @Operation(summary = "소프트웨어 코드 & 명으로 소프트웨어 정보 검색")
    @PostMapping("/search")
    public ComResponseDto<?> searchSoftwareInfo(@RequestBody SearchSoftwareDto searchSoftwareDto) {
        return responseUtil.setResponse200ok(softwareService.searchSoftwareInfo(searchSoftwareDto));
    }

    @Operation(summary = "소프트웨어 정보 저장")
    @PostMapping("/store")
    public ComResponseDto<?> storeSoftwareInfo(@RequestBody SoftwareInfoDto softwareInfoDto) {
        return responseUtil.setResponse200ok(softwareService.storeSoftwareInfo(softwareInfoDto));
    }

    @Operation(summary = "소프트웨어 정보 삭제 (단, 등록된 협력사가 존재할 경우 삭제 불가)")
    @DeleteMapping("/remove")
    public ComResponseDto<?> removeSoftwareInfo(@RequestBody SoftwareRemoveResultDto removedDto) {
        return responseUtil.setResponse200ok(softwareService.removeSoftwareInfo(removedDto));
    }

    @Operation(summary = "소프트웨어 코드 중복 조회")
    @GetMapping("/check/{code}")
    public ComResponseDto<?> checkExistSoftwareCode(@PathVariable("code") String code) {
        return responseUtil.setResponse200ok(softwareService.checkExistSoftwareCode(code));
    }

}
