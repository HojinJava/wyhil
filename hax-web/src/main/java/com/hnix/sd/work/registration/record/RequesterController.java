package com.hnix.sd.work.registration.record;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.registration.record.service.RequesterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Requester Controller", description = "작업 등록 시, 요청자 조회 및 추가 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/work/registration/requester")
@RestController
public class RequesterController {

    private final RequesterService service;
    private final ComResponseUtil comResponseUtil;


    @Operation(summary = "요청자 목록 전체 조회")
    @GetMapping("/list/{deptCd}/{isRequester}")
    public ComResponseDto<?> getRequesterAll(
                @PathVariable("deptCd") String deptCode,
                @PathVariable("isRequester") Character isRequester
            ) {
        return comResponseUtil.setResponse200ok( service.getRequesterByDeptCd(deptCode, isRequester) );
    }

}
