package com.hnix.sd.common.code;

import com.hnix.sd.common.code.dto.CodeRegisterDto;
import com.hnix.sd.common.code.dto.CodeRemoveDto;
import com.hnix.sd.common.code.dto.MultiGroupCodeIdDto;
import com.hnix.sd.common.code.service.CodeService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Code Controller", description = "Common Code Management")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/common/code")
public class CodeController {

    private final CodeService codeService;
    private final ComResponseUtil comResponseUtil;

    @Operation(summary = "그룹 코드 전체 조회")
    @GetMapping("/all")
    public ComResponseDto<?> getGroupCodeListAll() {
        return comResponseUtil.setResponse200ok(codeService.getGroupCodeListAll());
    }

    @Operation(summary = "중복 제거된 그룹 코드 목록 조회")
    @GetMapping("/list")
    public ComResponseDto<?> getGroupCodeList() {
        return comResponseUtil.setResponse200ok(codeService.getGroupCodeList());
    }

    @Operation(summary = "그룹 코드를 기준으로 서브 코드 목록 조회")
    @GetMapping("/list/{group}")
    public ComResponseDto<?> getSubCodeFromGroupCodeId(@PathVariable("group") String groupCodeCd) {
        return comResponseUtil.setResponse200ok(codeService.getSubCodeFromGroupCodeCd(groupCodeCd));
    }

    @Operation(summary = "여러 개의 그룹 코드를 기준으로 서비스 코드 목록 조회")
    @PostMapping("/list/group/multi")
    public ComResponseDto<?> getSubCodeFromMultiGroupCodeId(@RequestBody MultiGroupCodeIdDto multiGroupCodeIdDto) {
        return comResponseUtil.setResponse200ok(codeService.getSubCodeFromMultiGroupCodeId(multiGroupCodeIdDto));
    }

    @Operation(summary = "그룹 코드, 서브 코드 등록 및 수정")
    @PostMapping("/store")
    public ComResponseDto<?> storeGroupCode(@RequestBody CodeRegisterDto req) {
        return comResponseUtil.setResponse200ok(codeService.storeGroupCode(req.getUserId(), req.getGroupCode(), req.getSubCodes()));
    }

    @Operation(summary = "그룹 코드 삭제")
    @PostMapping("/remove/group")
    public ComResponseDto<?> removeGroupCode(@RequestBody CodeRemoveDto req) {
        codeService.removeGroupCode(req.getGroupCode());
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "그룹 코드의 서브 코드 삭제")
    @PostMapping("/remove/sub-code")
    public ComResponseDto<?> removeCode(@RequestBody CodeRemoveDto req) {
        codeService.removeSubCode(req.getGroupCode(), req.getSubCodeNames());
        return comResponseUtil.setResponse200ok();
    }

}
