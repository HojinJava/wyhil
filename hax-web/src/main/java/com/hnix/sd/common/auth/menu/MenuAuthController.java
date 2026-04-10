package com.hnix.sd.common.auth.menu;

import com.hnix.sd.common.auth.menu.dto.LoginUserIdDto;
import com.hnix.sd.common.auth.menu.dto.RegisterMenuAuthDto;
import com.hnix.sd.common.auth.menu.dto.RemoveMenuAuthDto;
import com.hnix.sd.common.auth.menu.service.MenuAuthService;
import com.hnix.sd.common.auth.menu.service.MenuAuthTreeService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name ="Menu Auth Controller", description = "메뉴 권한 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/auth/menu")
@RestController
public class MenuAuthController {

    private final MenuAuthService menuAuthService;
    private final MenuAuthTreeService menuAuthTreeService;
    private final ComResponseUtil responseUtil;


    @Operation(summary = "사용자에게 할당된 메뉴 권한 목록 조회")
    @PostMapping("/user")
    public ComResponseDto<?> loginUserMenuAccess(@RequestBody LoginUserIdDto dto) {
        return responseUtil.setResponse200ok(menuAuthTreeService.loginUserMenuAccess( dto.getUserId() ));
    }

    @Operation(summary = "메뉴에 할당된 권한 목록 조회")
    @GetMapping("/{groupCd}/list")
    public ComResponseDto<?> getMenuAuthListByGroupCd(@PathVariable("groupCd") String groupCd) {
        return responseUtil.setResponse200ok(menuAuthService.getMenuAuthListByGroupCd(groupCd));
    }

    @Operation(summary = "메뉴에 권한 할당")
    @PostMapping("/store")
    public ComResponseDto<?> updateMultipleMenuAuth(@RequestBody RegisterMenuAuthDto registerMenuAuthDto) {
        return responseUtil.setResponse200ok(menuAuthService.updateMultipleMenuAuth(registerMenuAuthDto.getMenuAuthList()));
    }

    @Operation(summary = "메뉴에서 권한 제거")
    @PostMapping("/remove")
    public ComResponseDto<?> deleteMultipleMenuAuth(@RequestBody RemoveMenuAuthDto removeMenuAuthDto) {
        menuAuthService.deleteMultipleMenuAuth(removeMenuAuthDto.getMenuAuthCdList());
        return responseUtil.setResponse200ok();
    }

}
