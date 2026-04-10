package com.hnix.sd.common.department;

import com.hnix.sd.common.department.dto.history.DepartmentStructureSearchDto;
import com.hnix.sd.common.department.dto.history.DeptSearchParamsDto;
import com.hnix.sd.common.department.service.DepartmentStructureService;
import com.hnix.sd.common.department.service.DeptResourceService;
import com.hnix.sd.common.department.service.DeptTreeService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "DeptResourceController", description = "회사 & 사업부 & 부서 정보조회 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/dept/resource")
@RestController
public class DeptFinderController {

    private final DeptTreeService treeService;
    private final DeptResourceService deptService;
    private final DepartmentStructureService historyService;

    private final ComResponseUtil comResponseUtil;


    /** 회사 & 사업부 & 부서관련 정보 조회 */
    @Operation(summary = "부서 코드로 부서 조회")
    @GetMapping("/{deptCd}")
    public ComResponseDto<?> getDepartmentFromCode(@PathVariable("deptCd") String deptCd) {
        return comResponseUtil.setResponse200ok(deptService.getDepartmentFromCode(deptCd));
    }

    @Operation(summary = "부서 정보 조회 시, 회사명과 함께 조회")
    @GetMapping("/company-with/name")
    public ComResponseDto<?> getDeptWithCompanyName() {
        return comResponseUtil.setResponse200ok(deptService.getDeptWithCompanyName());
    }

    @Operation(summary = "회사 목록 조회")
    @PostMapping("/company/all")
    public ComResponseDto<?> getCompanyList(@RequestBody DeptSearchParamsDto deptSearchParamDto) {
        return comResponseUtil.setResponse200ok(deptService.getCompanyList(deptSearchParamDto.getCompTypeCd()));
    }

    @Operation(summary = "부서 코드로 회사 조회")
    @GetMapping("/company/{deptCd}")
    public ComResponseDto<?> getCompanyFromCode(@PathVariable("deptCd") String deptCd) {
        return comResponseUtil.setResponse200ok(deptService.getCompanyFromCode(deptCd));
    }

    @Operation(summary = "부서(회사) 코드로 소속 부서 목록 조회")
    @GetMapping("/company/list/{deptCd}")
    public ComResponseDto<?> getDepartmentListFromCompany(@PathVariable("deptCd") String deptCd) {
        return comResponseUtil.setResponse200ok(deptService.getDepartmentListFromCompany(deptCd));
    }


    /** Department Tree 관련 */
    @Operation(summary = "회사 & 부서 Tree UI 데이터 조회")
    @GetMapping("/tree")
    public ComResponseDto<?> getTreeDataFromDepartment() {
        return comResponseUtil.setResponse200ok(treeService.getTreeDataFromDepartment());
    }

    @Operation(summary = "회사 & 부서 Code로 해당 Tree만 조회.")
    @GetMapping("/tree/{deptCd}")
    public ComResponseDto<?> getDepartmentParents(@PathVariable("deptCd") String deptCd) {
        return comResponseUtil.setResponse200ok(treeService.getDepartmentParents(deptCd));
    }

    @Operation(summary = "관리 고객사(COMP_TYPE_CD='M') 조회")
    @GetMapping("/tree/management")
    public ComResponseDto<?> getManagementDepartments() {
        return comResponseUtil.setResponse200ok(treeService.getManagementDepartments());
    }

    /** 회사 & 사업부 & 부서 정보를 부모 요소와 함께 확인 할 수 있는 API */

    @Operation(summary = "관리부서와 사업부를 상위 요소와 함께 조회")
    @GetMapping("/history/list")
    public ComResponseDto<?> getManageWithDepartment() {
        return comResponseUtil.setResponse200ok(historyService.getManageWithDepartment());
    }

    @Operation(summary = "관리부서 또는 사업부 정보 조회 시, 상위 요소를 함께 조회.")
    @GetMapping("/history/list/{typeCd}")
    public ComResponseDto<?> getDepartmentHistoryFromDeptType(@PathVariable("typeCd") Character typeCd) {
        return comResponseUtil.setResponse200ok(historyService.getDepartmentHistoryFromDeptType(typeCd));
    }

    @Operation(summary = "특정 회사 소속의 관리부서 목록 조회 ")
    @PostMapping("/history/search")
    public ComResponseDto<?> getManageDeptFromCompanyCd(@RequestBody DepartmentStructureSearchDto historySearchDto) {
        return comResponseUtil.setResponse200ok(
                historyService.getManageDeptFromCompanyCd(historySearchDto.getCompanyCd(), historySearchDto.getDeptTypeCd()));
    }


    //회사 & 부서 공통 Dialog에서 사용하는 API
    @Operation(summary = "회사 & 부서 공통 팝업, 목록 조회")
    @GetMapping("/popup/dept-all/{compTypeCd}")
    public ComResponseDto<?> getDepartmentListAll(@PathVariable("compTypeCd") Character compTypeCd) {
        return comResponseUtil.setResponse200ok(deptService.getDepartmentListAll(compTypeCd));
    }

    @Operation(summary = "회사 & 부서 공통 팝업, 검색")
    @GetMapping("/popup/search/{keyword}")
    public ComResponseDto<?> searchDeptFromKeyword(@PathVariable("keyword") String keyword) {
        return comResponseUtil.setResponse200ok(deptService.searchDeptFromKeyword(keyword));
    }

}
