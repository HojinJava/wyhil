package com.hnix.sd.work.software.contract.service;

import com.hnix.sd.common.auth.user.dao.UserAuthDao;
import com.hnix.sd.common.code.dto.SubCodeDto;
import com.hnix.sd.common.code.service.CodeService;
import com.hnix.sd.common.department.service.DepartmentStructureService;
import com.hnix.sd.core.dto.PageRequestDto;
import com.hnix.sd.core.dto.PageResponseDto;
import com.hnix.sd.work.software.contract.dao.ContractDao;
import com.hnix.sd.work.software.contract.dto.ContractDetailDto;
import com.hnix.sd.work.software.contract.dto.ContractGridInfoDto;
import com.hnix.sd.work.software.contract.dto.ContractSearchDialogDto;
import com.hnix.sd.work.software.contract.dto.ContractSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContractGridService {

    private final UserAuthDao userAuthDao;
    private final DepartmentStructureService departmentService;
    private final CodeService codeService;
    private final ContractDao contractDao;

    public PageResponseDto<ContractGridInfoDto> getContractPagination(ContractSearchDto searchDto) {
        List<SubCodeDto> partnerContractList = codeService.getSubCodeFromGroupCodeCd("COMP_CONTRACT");
        Map<String, String> partnerContractMap = partnerContractList.stream()
            .collect(Collectors.toMap(SubCodeDto::getCodeCd, SubCodeDto::getCodeText));

        int pageNo = searchDto.getPageNo();
        int pageSize = searchDto.getPageSize();
        searchDto.setOffset(pageNo * pageSize);
        searchDto.setLimit(pageSize);

        List<ContractGridInfoDto> results = contractDao.findContractPagination(searchDto);
        long count = contractDao.countContractPagination(searchDto);

        results.forEach(dto -> {
            String codeText = partnerContractMap.get(dto.getPartnerContractCd());
            dto.setCodeText(codeText);
        });

        PageRequestDto pageRequest = PageRequestDto.builder()
            .page(pageNo)
            .size(pageSize)
            .build();
        return new PageResponseDto<>(results, pageRequest, count);
    }

    public List<ContractGridInfoDto> getContractDialogGrid(ContractSearchDialogDto searchDto) {
        List<String> userAuths = userAuthDao.findByGroupCdWithUserId(searchDto.getLoginUserId());
        final String companyCd = departmentService.getDepartmentByDeptCd(searchDto.getLoginDeptCd());

        Map<String, Object> params = new HashMap<>();
        params.put("contractYear", searchDto.getContractYear());
        params.put("customerName", searchDto.getCustomerName());
        params.put("departmentName", searchDto.getDepartmentName());
        params.put("partnerName", searchDto.getPartnerName());
        params.put("softwareName", searchDto.getSoftwareName());
        params.put("subCode", searchDto.getSubCode());
        params.put("userAuths", userAuths);
        params.put("loginUserId", searchDto.getLoginUserId());
        params.put("loginUserCompanyCd", companyCd);

        return contractDao.findContractDialogGrid(params);
    }

    public ContractDetailDto getContractByContractNo(final String contractNo) {
        return contractDao.findContractDetailByContractNo(contractNo);
    }

}