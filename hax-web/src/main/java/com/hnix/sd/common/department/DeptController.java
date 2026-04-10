package com.hnix.sd.common.department;

import com.hnix.sd.common.department.dto.manage.DeptRegisterDto;
import com.hnix.sd.common.department.dto.manage.DeptRemoveDto;
import com.hnix.sd.common.department.dto.search.MultipleCodeDto;
import com.hnix.sd.common.department.dto.manage.MultipleRegisterDto;
import com.hnix.sd.common.department.service.DeptService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Department Controller", description = "회사 & 사업부 & 부서 정보 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/dept")
@RestController
public class DeptController {

    private final DeptService deptService;
    private final ComResponseUtil comResponseUtil;


    /** Department CRUD 및 중복 체크 */
    @Operation(summary = "회사 & 부서 정보 저장")
    @PostMapping("/store")
    public ComResponseDto<?> storeDepartment(@RequestBody DeptRegisterDto registerDto) {
        deptService.storeDepartment(registerDto);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "N개의 회사 & 부서 정보 수정 및 추가")
    @PostMapping("/store/multiple")
    public ComResponseDto<?> updateMultipleDepartment(@RequestBody MultipleRegisterDto deptRegister) {
        return comResponseUtil.setResponse200ok(deptService.updateMultipleDepartment(deptRegister.getDeptRegisterList()));
    }

    @Operation(summary = "회사 & 부서 정보 제거")
    @PostMapping("/remove")
    public ComResponseDto<?> removeDepartments(@RequestBody DeptRemoveDto deptRemoveDto) {
        return comResponseUtil.setResponse200ok(deptService.removeDepartments( deptRemoveDto ));
    }

    @Operation(summary = "회사 & 부서 코드 중복 확인")
    @GetMapping("/check/duplicate/{deptCd}")
    public ComResponseDto<?> checkDuplicateDeptCd(@PathVariable("deptCd") String deptCd) {
        return comResponseUtil.setResponse200ok(deptService.checkDuplicateDeptCd(deptCd));
    }

    @Operation(summary = "N개의 회사 & 부서 코드의 중복 확인")
    @PostMapping("/check/duplicates")
    public ComResponseDto<?> checkMultipleDuplicateDeptCd(@RequestBody MultipleCodeDto codeDto) {
        return comResponseUtil.setResponse200ok(deptService.checkMultipleDuplicateDeptCd(codeDto.getCodes()));
    }

}
