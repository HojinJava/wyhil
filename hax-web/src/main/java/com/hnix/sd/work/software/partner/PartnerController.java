package com.hnix.sd.work.software.partner;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.software.partner.dto.PartnerCodeDto;
import com.hnix.sd.work.software.partner.dto.PartnerInfoDto;
import com.hnix.sd.work.software.partner.dto.PartnerRemoveDto;
import com.hnix.sd.work.software.partner.dto.PartnerSearchKeywordDto;
import com.hnix.sd.work.software.partner.service.PartnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partner Controller", description = "소프트웨어 협력사 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/work/partner")
@RestController
public class PartnerController {

    private final PartnerService partnerService;
    private final ComResponseUtil responseUtil;


    @Operation(summary = "소프트웨어 협력사 전체 조회")
    @GetMapping("/all")
    public ComResponseDto<?> getPartnerListAll() {
        return responseUtil.setResponse200ok(partnerService.getPartnerListAll());
    }

    @Operation(summary = "소프트웨어 협력사 검색 (조건: 협력사명, SUB CODE, SW 명) ")
    @PostMapping("/search")
    public ComResponseDto<?> searchPartnerWithNames(@RequestBody PartnerSearchKeywordDto keywordDto) {
        return responseUtil.setResponse200ok(partnerService.searchPartnerWithNames(keywordDto));
    }

    @Operation(summary = "소프트웨어 협력사 ID로 정보 조회")
    @PostMapping("/info")
    public ComResponseDto<?> getPartnerInfoFromSubCode(@RequestBody PartnerCodeDto codeDto) {
        return responseUtil.setResponse200ok(partnerService.getPartnerInfoFromSubCode(codeDto));
    }

    @Operation(summary = "소프트웨어 협력사 정보 수정 및 추가")
    @PostMapping("/store")
    public ComResponseDto<?> updatePartnerInfo(@RequestBody PartnerInfoDto partnerInfoDto) {
        partnerService.updatePartnerInfo(partnerInfoDto);
        return responseUtil.setResponse200ok();
    }

    @Operation(summary = "소프트웨어 협력사 정보 제거")
    @PostMapping("/remove")
    public ComResponseDto<?> removePartnerInfo(@RequestBody PartnerRemoveDto removeDto) {
        partnerService.removePartnerInfo(removeDto);
        return responseUtil.setResponse200ok();
    }

    @Operation(summary = "SEQ 생성을 위해, SW code로 시작하는 Sub Code 개수 count")
    @GetMapping("/count/{swCode}")
    public ComResponseDto<?> findSubCodeSeqWithSwCode(@PathVariable("swCode") String swCode) {
        return responseUtil.setResponse200ok( partnerService.findSubCodeSeqWithSwCode(swCode) );
    }


    @Operation(summary = "소프트웨어 협력사 코드 중복 조회")
    @GetMapping("/check/duplicate/{subCode}")
    public ComResponseDto<?> checkExistBySubCode(@PathVariable("subCode") String subCode) {
        return responseUtil.setResponse200ok(partnerService.checkExistBySubCode(subCode));
    }
}
