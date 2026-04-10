package com.hnix.sd.common.menu;

import com.hnix.sd.common.menu.dto.DuplicateMenuCodeDto;
import com.hnix.sd.common.menu.dto.MenuRegisterDto;
import com.hnix.sd.common.menu.dto.RemoveMenuDto;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.common.menu.service.MenuService;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Menu Controller", description = "메뉴 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/menu")
@RestController
public class MenuController {

    private final MenuService menuService;
    private final ComResponseUtil responseUtil;


    @Operation(summary = "메뉴 데이터 전체 조회")
    @GetMapping("/all")
    public ComResponseDto<?> getTreeDataFromMenu() {
        return responseUtil.setResponse200ok(menuService.getTreeDataFromMenu());
    }

    @Operation(summary = "메뉴 선택 조회")
    @GetMapping("/{menuCd}")
    public ComResponseDto<?> getMenuInfoById(@PathVariable("menuCd") String menuCd) {
        return responseUtil.setResponse200ok(menuService.getMenuInfoById(menuCd));
    }

    @Operation(summary = "메뉴 저장")
    @PostMapping("/store")
    public ComResponseDto<?> updateMultipleMenu(@RequestBody MenuRegisterDto registerDto) {
        return responseUtil.setResponse200ok(menuService.updateMultipleMenu(registerDto.getMenuList()));
    }

    @Operation(summary = "메뉴 Codes 중복 체크")
    @PostMapping("/check/duplicates")
    public ComResponseDto<?> checkDuplicateCodes(@RequestBody DuplicateMenuCodeDto menuCodesDto) {
        return responseUtil.setResponse200ok(menuService.checkDuplicateCodes(menuCodesDto.getMenuCodes()));
    }

    @Operation(summary = "메뉴 삭제")
    @PostMapping("/remove")
    public ComResponseDto<?> removeMenus(@RequestBody RemoveMenuDto removeMenuDto) {
        menuService.removeMenus(removeMenuDto);
        return responseUtil.setResponse200ok();
    }

}
