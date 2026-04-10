package com.hnix.sd.work.software.contract.service;

import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.work.software.contract.dao.ContractDao;
import com.hnix.sd.work.software.contract.dao.ContractSubDao;
import com.hnix.sd.work.software.contract.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContractService {

    private final ContractDao contractDao;
    private final ContractSubDao contractSubDao;
    private final ContractHistoryService contractHistoryService;

    private Set<String> existContractNumbers;

    @Transactional
    public List<FailedRegisterDto> registerMultiContractInfo(List<RegisterContractDto> contractList) {
        if (contractList.isEmpty()) {
            return new ArrayList<>();
        }

        var firstContractObj = contractList.get(0);

        final LocalDateTime startDateTime = firstContractObj.getContractStartDate();
        final LocalDateTime endDateTime = firstContractObj.getContractEndDate();

        existContractNumbers = contractDao.findByContractNoStartingWith( firstContractObj.getSubCd() );

        List<FailedRegisterDto> failedList = new ArrayList<>();
        List<ContractDto> entities = new ArrayList<>();

        for (var contract : contractList) {
            final String subCode = contract.getSubCd();
            final String deptCode = contract.getDeptCd();
            final String year = contract.getContractYear();
            int contractNumber = 1;
            if ( checkVariablesEmpty(contract) ) {
                failedList.add(
                        new FailedRegisterDto(subCode, deptCode, "empty"));
                continue;
            }

            String newContractNo = createContractNo(subCode, deptCode, year, contractNumber++);

            while ( existContractNumbers.contains(newContractNo) ) {
                if (contractNumber > 9999) {
                    throw new BizException("생성 가능한 Contract No 개수를 초과하였습니다.");
                }
                newContractNo = createContractNo(subCode, deptCode, year, contractNumber++);
            }

            entities.add(toContractDto(contract, newContractNo, startDateTime, endDateTime, 'N'));
        }

        for (ContractDto dto : entities) {
            contractDao.insertContract(dto);
        }
     
        for (ContractDto entity : entities) {
            // Sub 테이블
            contractSubDao.insertByContractNo(entity.getContractNo());
        }

        existContractNumbers.clear();

        return failedList;
    }

    @Transactional
    public List<FailedRegisterDto> registerContractInfo(RegisterContractDto registerContractDto) {

        final LocalDateTime startDateTime = registerContractDto.getContractStartDate();
        final LocalDateTime endDateTime = registerContractDto.getContractEndDate();

        List<FailedRegisterDto> failedList = new ArrayList<>();
        final String contractNo = registerContractDto.getContractNo();

        ContractDto dto = toContractDto(registerContractDto, contractNo, startDateTime, endDateTime, 'N');
        contractDao.insertContract(dto);

        // Sub 테이블
        contractSubDao.insertByContractNo(dto.getContractNo());

        return failedList;
    }

    public UpdateContractDto updateContractInfo(UpdateContractDto updateDto) {
        ContractDto entity = contractDao.findById(updateDto.getContractNo()).orElseThrow(() -> new BizException("계약번호가 존재하지 않습니다: " + updateDto.getContractNo()));

        entity.setRemark(updateDto.getRemark());
        entity.setContractStartDate(updateDto.getContractStartDate());
        entity.setContractEndDate(updateDto.getContractEndDate());
        entity.setModDt(LocalDateTime.now());
        entity.setModId(updateDto.getModId());

        contractDao.updateContract(entity);
        
        return updateDto;
    }


    private boolean checkVariablesEmpty(RegisterContractDto contractDto) {
        return StringUtils.isEmpty( contractDto.getSubCd() )
                || StringUtils.isEmpty( contractDto.getDeptCd() )
                || StringUtils.isEmpty( contractDto.getContractYear() );
    }

    private String createContractNo(final String subCd, final String deptCd, final String contractYear, final int number) {
        return String.format("%s_%s_%s%04d", subCd, deptCd, contractYear, number);
    }

    @Transactional
    public void removeContractInfo(RemoveContractDto removeContractDto) {
        ContractDto entity = contractDao.findById(removeContractDto.getContractNo()).orElse(null);
        if (entity == null) return;
        
        contractHistoryService.deleteContractHistory(entity, removeContractDto);

        /* 유지보수 이력 관리에 데이터가 남아있어야 하기에, 데이터 실제 삭제 X */
        contractDao.updateContractDeleteYn(entity.getContractNo());
    }

    @Transactional
    public void removeMultiContractInfo(RemoveMultiContractDto contractDto) {
        Set<String> contractIds = contractDto.getContractIds();
        if (contractIds.isEmpty()) return;
        
        List<ContractDto> entities = contractDao.findAllByContractNos(contractIds);

        for (ContractDto entity : entities) {
            contractHistoryService.deleteContractMultiHistory(entity, contractDto);
        }

        contractDao.updateMultiContractDeleteYn(contractIds);
    }
    
    private ContractDto toContractDto(RegisterContractDto regDto, String contractNo, LocalDateTime start, LocalDateTime end, Character deleteYn) {
        return ContractDto.builder()
                .contractNo(contractNo)
                .subCd(regDto.getSubCd())
                .deptCd(regDto.getDeptCd())
                .contractYear(regDto.getContractYear())
                .contractStartDate(start)
                .contractEndDate(end)
                .remark(regDto.getRemark())
                .deleteYn(deleteYn)
                .regId(regDto.getRegId())
                .regDt(LocalDateTime.now())
                .build();
    }

}
