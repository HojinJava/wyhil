package com.hnix.sd.common.user;

import com.hnix.sd.common.user.dto.*;
import com.hnix.sd.common.user.service.UserPageService;
import com.hnix.sd.common.user.service.UserService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User Controller", description = "사용자 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/user")
@RestController
public class UserController {

    private final UserService userService;
    private final UserPageService userPageService;
    private final ComResponseUtil comResponseUtil;



    @Operation(summary = "사용자 비밀번호 초기화 여부")
    @GetMapping("/pwreset/check/{userEmail}")
    public ComResponseDto<?> checkPwReset(@PathVariable("userEmail") String userEmail) {
        return comResponseUtil.setResponse200ok(userService.checkPwReset(userEmail));
    }


    @Operation(summary = "사용자 전체 조회 (회사/사업부 동시 조회)")
    @GetMapping("/all")
    public ComResponseDto<?> getAllUserInfoWithDept() {
        return comResponseUtil.setResponse200ok(userService.getAllUserInfoWithDept());
    }

    @Operation(summary = "사용자 조회")
    @PostMapping("")
    //public ComResponseDto<?> getUserInfoWithDept(@RequestBody UserSelectDto userSelectDto) {
    public ComResponseDto<?> getUserInfoWithDept(@RequestBody Map<String, Object> mapParam) {

        System.out.println("# [UserController.java] getUserInfoWithDept() run");
        System.out.println("# [UserController.java] mapParam : " + mapParam.toString());
        //UserId : bkkim@n-__link.co.kr

        return comResponseUtil.setResponse200ok(userService.getUserInfoWithDept(mapParam));
    }

    @Operation(summary = "회사/부서 또는 사용자명 기준 사용자 Pagination 조회")
    @GetMapping("/page")
    public ComResponseDto<?> getUserWithDeptPagination(@RequestParam(name = "page", defaultValue = "0") int page,
                                                       @RequestParam(name = "size", defaultValue = "15") int size,
                                                       @RequestParam(name = "sortBy", defaultValue = "") String sort,
                                                       @RequestParam(name = "deptCd", defaultValue = "") String deptCd,
                                                       @RequestParam(name = "userNm", defaultValue = "") String userNm,
                                                       @RequestParam(name = "userEmail", defaultValue = "") String userEmail) {
        return comResponseUtil.setResponse200ok(
                userPageService.getUserWithDeptPagination(page, size, sort, deptCd, userNm, userEmail));
    }

    @Operation(summary = "사용자 추가")
    @PostMapping("/regist")
    public ComResponseDto<?> registerUserInfo(@RequestBody UserRegistDto registerDto) {
        return comResponseUtil.setResponse200ok(userService.registerUserInfo(registerDto));
    }

    @Operation(summary = "사용자 정보 업데이트")
    @PostMapping("/update")
    public ComResponseDto<?> updateUserInfo(@RequestBody ChangeUserInfoDto registerDto) {
        return comResponseUtil.setResponse200ok(userService.updateUserInfo(registerDto));
    }

    @Operation(summary = "사용자 최초 로그인 시, 사용자 정보 업데이트")
    @PostMapping("/update/setup")
    public ComResponseDto<?> setupUserInfo(@RequestBody SetupUserInfoDto updateDto) {
        return comResponseUtil.setResponse200ok(userService.setupUserInfo(updateDto));
    }

    @Operation(summary = "")
    @PostMapping("/update/pw")
    public ComResponseDto<?> updateUserPw(@RequestBody SetupUserInfoDto updateDto) {
        return comResponseUtil.setResponse200ok(userService.updateUserPw(updateDto));
    }


    @Operation(summary = "로그인 사용자 정보 업데이트")
    @PostMapping("/update/login-user")
    public ComResponseDto<?> updateLoginUserInfo(@RequestBody UpdateLoginUserDto registerDto) {
        return comResponseUtil.setResponse200ok(userService.updateLoginUserInfo(registerDto));
    }

    @Operation(summary = "중복 E-mail 확인")
    @PostMapping("/check/email")
    public ComResponseDto<?> checkDuplicateEmail(@RequestBody UserDuplicateDto userEmail) {
        return comResponseUtil.setResponse200ok(userService.checkDuplicateEmail(userEmail));
    }

    @Operation(summary = "사용자 제거")
    @PostMapping("/remove")
    public ComResponseDto<?> removeUsers(@RequestBody UserRemoveDto userRemoveDto) {
        userService.removeUsers(userRemoveDto);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "사용자 삭제")
    @PostMapping("/update/delete")
    public ComResponseDto<?> softDeleteUser(@RequestBody UserDeleteDto userDeleteDto) {
        userService.softDeleteUsers(userDeleteDto);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "사용자 권한 그룹 조회")
    @PostMapping("/auth-group")
    public ComResponseDto<?> getAuthGroupUser(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("user_id");
        return comResponseUtil.setResponse200ok(userService.getAuthGroupUser(userId));
    }

    @Operation(summary = "이메일로 요청자 조회")
    @PostMapping("/requester-by-email")
    public ComResponseDto<?> getRequesterByEmail(@RequestBody Map<String, Object> params) {
        String userEmail = (String) params.get("userEmail");
        return comResponseUtil.setResponse200ok(userService.getRequesterByEmail(userEmail));
    }

}
