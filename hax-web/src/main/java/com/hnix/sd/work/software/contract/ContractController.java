package com.hnix.sd.work.software.contract;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.software.contract.dto.*;
import com.hnix.sd.work.software.contract.service.ContractGridService;
import com.hnix.sd.work.software.contract.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Contract Controller", description = "소프트웨어 협력사 계약 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/work/contract")
@RestController
public class ContractController {

    private final ContractGridService contractGridService;
    private final ContractService contractService;
    private final ComResponseUtil comResponseUtil;

    @Operation(summary = "계약 등록")
    @PostMapping("/multi/register")
    public ComResponseDto<?> registerMultiContractInfo(@RequestBody RegisterContractListDto contractListDto) {
        contractService.registerMultiContractInfo( contractListDto.getContractList() );
        return comResponseUtil.setResponse200ok();
    }


    //work/contract/page
    @Operation(summary = "계약 목록 Pagination 조회")
    @PostMapping("/page")
    public ComResponseDto<?> getContractWithPagination(@RequestBody ContractSearchDto searchDto) {
        return comResponseUtil.setResponse200ok( contractGridService.getContractPagination(searchDto) );
    }

    @Operation(summary = "계약 정보 한 건 조회")
    @PostMapping("")
    public ComResponseDto<?> getContractInfoByNumber(@RequestBody ContractNumberDto contractNumberDto) {
        return comResponseUtil.setResponse200ok( contractGridService.getContractByContractNo( contractNumberDto.getContractNo()) );
    }

    @Operation(summary = "계약 정보 수정")
    @PostMapping("/update")
    public ComResponseDto<?> updateContractInfo(@RequestBody UpdateContractDto updateContractDto) {
        contractService.updateContractInfo( updateContractDto );
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "계약 정보 삭제")
    @PostMapping("/remove")
    public ComResponseDto<?> removeContractInfo(@RequestBody RemoveContractDto contractDto) {
        contractService.removeContractInfo(contractDto);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "계약 정보 N개 삭제")
    @PostMapping("/multi/remove")
    public ComResponseDto<?> removeMultiContractInfo(@RequestBody RemoveMultiContractDto contractIds) {
        contractService.removeMultiContractInfo(contractIds);
        return comResponseUtil.setResponse200ok();
    }

    @Operation(summary = "계약 정보 Dialog 조회")
    @PostMapping("/dialog/page")
    public ComResponseDto<?> getContractInfoToDialog(@RequestBody ContractSearchDialogDto contractSearchDto) {
        return comResponseUtil.setResponse200ok( contractGridService.getContractDialogGrid(contractSearchDto) );
    }

}
