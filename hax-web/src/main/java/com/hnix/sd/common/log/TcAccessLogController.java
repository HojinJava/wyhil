package com.hnix.sd.common.log;

import com.hnix.sd.common.log.dto.AccessMenuDto;
import com.hnix.sd.common.log.dto.SearchAccessLogDto;
import com.hnix.sd.common.log.service.TcAccessLogService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Access Log Controller", description = "접속 로그 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/access/log")
@RestController
public class TcAccessLogController {

    private final TcAccessLogService accessLogService;
    private final ComResponseUtil responseUtil;

    @Operation(summary = "접속 로그 페이지 조회")
    @PostMapping("/page")
    public ComResponseDto<?> getAccessLogsInfo(@RequestBody SearchAccessLogDto accessLogDto) {
        return responseUtil.setResponse200ok( accessLogService.getAccessLogByPagination(accessLogDto) );
    }

    @Operation(summary = "접속 로그 저장")
    @PostMapping("/store")
    public ComResponseDto<?> storeAccessLog(@RequestBody AccessMenuDto logInfoDto) {
        accessLogService.insertAccessLog(logInfoDto.getMenuCd(), logInfoDto.getMenuType());
        return responseUtil.setResponse200ok();
    }

}
