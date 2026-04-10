package com.hnix.sd.work.software.manage.service;

import com.hnix.sd.common.history.dao.CommonHistoryDao;
import com.hnix.sd.work.software.manage.dao.SoftwareDao;
import com.hnix.sd.work.software.manage.dto.SearchSoftwareDto;
import com.hnix.sd.work.software.manage.dto.SoftwareDto;
import com.hnix.sd.work.software.manage.dto.SoftwareInfoDto;
import com.hnix.sd.work.software.manage.dto.SoftwareRemoveResultDto;
import com.hnix.sd.work.software.manage.dto.SoftwareStoreResultDto;
import com.hnix.sd.work.software.partner.dao.PartnerDao;
import com.hnix.sd.work.software.partner.dto.PartnerDto;
import com.hnix.sd.work.software.partner.dto.PartnerInfoDto;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SoftwareService {

    private final SoftwareDao softwareDao;
    private final PartnerDao partnerDao;
    private final SWHistoryService swHistoryService;
    private final CommonHistoryDao commonHistoryDao;


    public List<SoftwareInfoDto> getSoftwareListAll() {
        return softwareDao.findAll()
                .stream().map(this::convertToInfoDto)
                .collect(Collectors.toList());
    }


    public List<SoftwareInfoDto> searchSoftwareInfo(SearchSoftwareDto searchSoftwareDto) {
        return softwareDao.findBySwCodeAndSwName(searchSoftwareDto.getSwCode(), searchSoftwareDto.getSwName())
                .stream().map(this::convertToInfoDto)
                .collect(Collectors.toList());
    }

    public SoftwareStoreResultDto storeSoftwareInfo(SoftwareInfoDto softwareDto) {
        final String swCode = softwareDto.getSwCode();

        SoftwareDto entity = softwareDao.findBySwCode(swCode);
        boolean isNew = false;
        if (entity == null) {
            entity = new SoftwareDto();
            isNew = true;
        }
        
        swHistoryService.addSoftwareHistory(entity, softwareDto);

        if ( StringUtils.isEmpty(swCode) ) {
            return new SoftwareStoreResultDto(
                    softwareDto.getSwCode(),
                    softwareDto.getSwName(),
                    "FAIL",
                    "Code 값을 입력해주세요.");
        }

        entity.setSwCode(softwareDto.getSwCode());
        entity.setModId(softwareDto.getModId());
        entity.setSwName(softwareDto.getSwName());
        entity.setSwDesc(softwareDto.getSwDesc());

        if (isNew) {
            entity.setRegId(softwareDto.getModId());
            entity.setRegDt(LocalDateTime.now());
            softwareDao.insertSoftware(entity);
        } else {
            entity.setModDt(LocalDateTime.now());
            softwareDao.updateSoftware(entity);
        }

        return new SoftwareStoreResultDto(
                softwareDto.getSwCode(),
                softwareDto.getSwName(),
                "SUCCESS",
                "저장 성공");
    }

    @Transactional
    public SoftwareRemoveResultDto removeSoftwareInfo(SoftwareRemoveResultDto removedDto) {
        final String swCode = removedDto.getSwCode();

        if ( StringUtils.isEmpty(swCode) ) {
            return new SoftwareRemoveResultDto(
                    swCode,
                    "FAIL",
                    "Code 값이 존재하지 않습니다.",
                    "");
        }

        List<PartnerInfoDto> partnerInfoDtoList = checkExistPartner(swCode);

        if (!partnerInfoDtoList.isEmpty()) {
            return new SoftwareRemoveResultDto(
                    swCode,
                    "FAIL",
                    "해당 소프트웨어에 협력사가 등록되어 있습니다.",
                    partnerInfoDtoList.get(0).getSubCode());
        }

        softwareDao.deleteBySwCode( swCode );
        commonHistoryDao.deleteByTargetIdAndMenuCd(swCode, "work-software-manage");

        return new SoftwareRemoveResultDto(
                swCode,
                "SUCCESS", "", "");
    }

    public boolean checkExistSoftwareCode(final String swCode) {
        return softwareDao.existsBySwCode(swCode);
    }

    public List<PartnerInfoDto> checkExistPartner(String swCode) {
        return partnerDao.findExistingPartnerBySoftwareCode(swCode)
                .stream().map(this::convertToPartnerInfoDto)
                .collect(Collectors.toList());
    }

    private SoftwareInfoDto convertToInfoDto(SoftwareDto software) {
        if (software == null) return null;
        SoftwareInfoDto dto = new SoftwareInfoDto();
        dto.setSwCode(software.getSwCode());
        dto.setSwName(software.getSwName());
        dto.setSwDesc(software.getSwDesc());
        dto.setRegId(software.getRegId());
        dto.setRegDt(software.getRegDt());
        dto.setModId(software.getModId());
        dto.setModDt(software.getModDt());
        return dto;
    }

    private PartnerInfoDto convertToPartnerInfoDto(PartnerDto partner) {
        if (partner == null) return null;
        PartnerInfoDto dto = new PartnerInfoDto();
        dto.setSubCode(partner.getSubCode());
        dto.setSwCode(partner.getSwCode());
        dto.setCompCode(partner.getCompCode());
        dto.setPartnerTypeCd(partner.getPartnerTypeCd());
        dto.setPartnerContractCd(partner.getPartnerContractCd());
        dto.setContractCancelPossibleYn(partner.getContractCancelPossibleYn());
        dto.setUpgradePossibleYn(partner.getUpgradePossibleYn());
        dto.setTechnicalSupportYn(partner.getTechnicalSupportYn());
        dto.setVisitSupportYn(partner.getVisitSupportYn());
        dto.setRegularServiceCd(partner.getRegularServiceCd());
        dto.setSubscriptionYn(partner.getSubscriptionYn());
        dto.setRemark(partner.getRemark());
        dto.setRegId(partner.getRegId());
        dto.setRegDt(partner.getRegDt());
        dto.setModId(partner.getModId());
        dto.setModDt(partner.getModDt());
        return dto;
    }


}
