package com.hnix.sd.common.auth.group;

import com.hnix.sd.common.auth.group.dto.GroupAuthInfoDto;
import com.hnix.sd.common.auth.group.dto.RemoveGroupAuthDto;
import com.hnix.sd.common.auth.group.service.GroupAuthService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Group Auth Controller", description = "권한 그룹 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/auth/group")
@RestController
public class GroupAuthController {

    private final GroupAuthService groupAuthService;
    private final ComResponseUtil responseUtil;


    @Operation(summary = "그룹 권한 목록 전체 호출")
    @GetMapping("/all")
    public ComResponseDto<?> getGroupAuthList() {
        return responseUtil.setResponse200ok(groupAuthService.getGroupAuthList());
    }

    @Operation(summary = "그룹 권한 한 건 조회")
    @GetMapping("/code/{groupCd}")
    public ComResponseDto<?> getGroupAuthFromGroupCd(@PathVariable("groupCd") String groupCd) {
        return responseUtil.setResponse200ok(groupAuthService.getGroupAuthFromGroupCd(groupCd));
    }

    @Operation(summary = "그룹 권한 데이터 수정 및 추가")
    @PostMapping("/store")
    public ComResponseDto<?> storeGroupAuth(@RequestBody GroupAuthInfoDto groupAuthInfoDto) {
        return responseUtil.setResponse200ok(groupAuthService.storeGroupAuth(groupAuthInfoDto));
    }

    @Operation(summary = "그룹 권한 데이터 삭제")
    @PostMapping("/remove")
    public ComResponseDto<?> removeGroupAuth(@RequestBody RemoveGroupAuthDto removeGroupAuthDto) {
        groupAuthService.removeGroupAuth(removeGroupAuthDto.getGroupCd());
        return responseUtil.setResponse200ok();
    }

    @Operation(summary = "그룹 권한 코드 중복 체크")
    @GetMapping("/check/duplicate/{authCode}")
    public ComResponseDto<?> checkDuplicateAuthCode(@PathVariable("authCode") String authCode) {
        return responseUtil.setResponse200ok(groupAuthService.checkDuplicateAuthCode(authCode));
    }

}
