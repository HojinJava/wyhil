package com.hnix.sd.work.software.contract.service;

import com.hnix.sd.work.software.contract.dto.ContractDto;
import com.hnix.sd.work.software.contract.dto.RemoveContractDto;
import com.hnix.sd.work.software.contract.dto.RemoveMultiContractDto;
import com.hnix.sd.work.software.history.dto.HistoryStoreDto;
import com.hnix.sd.work.software.history.service.SoftwareHistoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ContractHistoryService {
  private final SoftwareHistoryService softwareHistoryService; 
  public static final String HISTORY_TYPE_DELETE = "HISTORY_TYPE_DELETE";

  public void deleteContractHistory(ContractDto contract, RemoveContractDto removeContractDto) {

    HistoryStoreDto historyStoreDto = new HistoryStoreDto();
    historyStoreDto.setContractNo(contract.getContractNo());
    historyStoreDto.setHistoryTypeCd(HISTORY_TYPE_DELETE);
    historyStoreDto.setHistoryContents("계약 정보 삭제에 따른 이력 자동 생성");
    historyStoreDto.setRegId(removeContractDto.getUserId());

    softwareHistoryService.storeMaintenanceHistory(historyStoreDto);
	}

  public void deleteContractMultiHistory(ContractDto contract, RemoveMultiContractDto removeMultiContractDto) {
		String hisType = HISTORY_TYPE_DELETE;

		String msg = "";

    HistoryStoreDto historyStoreDto = new HistoryStoreDto();
    historyStoreDto.setContractNo(contract.getContractNo());
    historyStoreDto.setHistoryTypeCd(hisType);
    historyStoreDto.setHistoryContents(msg);
    historyStoreDto.setRegId(removeMultiContractDto.getUserId());
    
    softwareHistoryService.storeMaintenanceHistory(historyStoreDto);
	}
	
}
