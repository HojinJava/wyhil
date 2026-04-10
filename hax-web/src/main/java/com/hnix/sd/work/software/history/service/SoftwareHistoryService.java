package com.hnix.sd.work.software.history.service;

import com.hnix.sd.common.code.dto.SubCodeDto;
import com.hnix.sd.common.code.service.CodeService;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.work.software.history.dao.SoftwareHistoryDao;
import com.hnix.sd.work.software.history.dto.*;
import com.hnix.sd.core.dto.PageRequestDto;
import com.hnix.sd.core.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SoftwareHistoryService {

    private final SoftwareHistoryDao historyDao;
    private final CodeService codeService;

    public String storeMaintenanceHistory(HistoryStoreDto storeDto) {
        if (StringUtils.isEmpty(storeDto.getContractNo())) {
            throw new BizException("이력 저장을 위한 필수값이 존재하지 않습니다.");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        final String dateTimeStr = String.format("HIS_%s", currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        SoftwareHistoryDto historyDto = SoftwareHistoryDto.builder()
                .hisNo(null)
                .contractNo(storeDto.getContractNo())
                .historyTypeCd(storeDto.getHistoryTypeCd())
                .historyContents(storeDto.getHistoryContents())
                .regId(storeDto.getRegId())
                .regDt(currentDateTime)
                .build();
        
        historyDao.insertHistory(historyDto);

        return dateTimeStr;
    }

    public Integer checkExistContractNo(final String contractNo) {
        return historyDao.countByContractNo(contractNo) + 1;
    }

    public void updateMaintenanceHistory(HistoryUpdateDto updateDto) {
        SoftwareHistoryDto history = historyDao.findById(updateDto.getHistoryId())
                .orElseThrow(() -> new BizException("해당 유지보수 이력이 존재하지 않습니다."));

        if (updateDto.getHistorySeq() != null && updateDto.getHistorySeq() > 0) {
            // history.setHistorySeq(updateDto.getHistorySeq()); 
        }
        if (!StringUtils.isEmpty(updateDto.getHistoryContents())) {
            history.setHistoryContents(updateDto.getHistoryContents());
        }

        // DTO 필드 업데이트 및 DAO 호출
        historyDao.updateHistory(history);
    }

    public Optional<SoftwareHistoryDto> getSoftwareHistoryFromId(String id) {
        return historyDao.findById(id);
    }

    public List<SoftwareHistoryDto> getSoftwareHistoryFromContractNo(String contractNo) {
        List<SubCodeDto> regularServiceList = codeService.getSubCodeFromGroupCodeCd("HISTORY_TYPE");
        Map<String, String> regularServiceMap = regularServiceList.stream()
                .collect(Collectors.toMap(SubCodeDto::getCodeCd, SubCodeDto::getCodeText));

        List<SoftwareHistoryDto> historyList = historyDao.findByContractNo(contractNo);

        historyList.forEach(history -> {
            String historyTypeText = regularServiceMap.get(history.getHistoryTypeCd());
            history.setHistoryTypeCd(historyTypeText);

            // 파일 정보 조회 로직 (기존 로직 유지)
            // history.setFileList(fileService.getFileList(history.getFileId()));
        });

        return historyList;
    }

    public void removeHistoryInfoById(String id) {
        historyDao.updateHistoryUseYn(id);
    }

    public PageResponseDto<HistoryGridInfoDto> getSoftwareHistoryByPagination(HistorySearchDto search) {
        if (StringUtils.isEmpty(search.getContractYear())) {
            search.setContractYear(Integer.toString(LocalDate.now().getYear()));
        }

        // MyBatis 페이징 처리를 위해 search 객체에 offset/limit 설정 (필요 시)
        int offset = (search.getPageNo() - 1) * search.getPageSize();
        // search.setOffset(offset); 
        
        List<HistoryGridInfoDto> list = historyDao.findSoftwareHistoryByPagination(search);
        long total = historyDao.countSoftwareHistoryByPagination(search);

        PageRequestDto pageRequest = PageRequestDto.builder()
            .page(search.getPageNo())
            .size(search.getPageSize())
            .build();

        return new PageResponseDto<>(list, pageRequest, total);
    }
}
