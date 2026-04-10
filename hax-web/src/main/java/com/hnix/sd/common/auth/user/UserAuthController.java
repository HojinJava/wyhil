package com.hnix.sd.common.auth.user;


import com.hnix.sd.common.auth.user.dto.FindUserAuthDto;
import com.hnix.sd.common.auth.user.dto.RegisterUserAuthDto;
import com.hnix.sd.common.auth.user.dto.RemoveUserAuthDto;
import com.hnix.sd.common.auth.user.service.UserAuthService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Auth Controller", description = "사용자 권한 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/auth/user")
@RestController
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final ComResponseUtil responseUtil;


    @Operation(summary = "사용자 & 회사/사업부 권한 목록 조회")
    @PostMapping("")
    public ComResponseDto<?> getUserAuthList(@RequestBody FindUserAuthDto userAuthDto) {
        return responseUtil.setResponse200ok(
                userAuthService.getUserAuthListFromGroupCd(userAuthDto.getGroupCd(), userAuthDto.getUserTypeCd()));
    }

//    @Operation(summary = "사용자 & 회사/부서 권한 목록 전체 조회")
//    @GetMapping("/all/{page}")
//    public ComResponseDto<?> getUserAuthListAll(@PathVariable("page") Integer page) {
//        return responseUtil.setResponse200ok( userAuthService.getUserAuthListAll(page) );
//    }

    @Operation(summary = "사용자 & 회사/사업부 권한 저장")
    @PostMapping("/store")
    public ComResponseDto<?> updatedMultipleUserAuth(@RequestBody RegisterUserAuthDto userAuthList) {
        return responseUtil.setResponse200ok(userAuthService.updatedMultipleUserAuth(userAuthList.getUserAuthList()));
    }

    @Operation(summary = "사용자 & 회사/사업부 권한 삭제")
    @PostMapping("/remove")
    public ComResponseDto<?> removeMultipleUserAuth(@RequestBody RemoveUserAuthDto userAuthIds) {
        userAuthService.removeMultipleUserAuth(userAuthIds.getUserAuthIds());
        return responseUtil.setResponse200ok();
    }

}