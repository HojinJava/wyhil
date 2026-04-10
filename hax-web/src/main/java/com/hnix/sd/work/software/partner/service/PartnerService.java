package com.hnix.sd.work.software.partner.service;

import com.hnix.sd.common.history.dao.CommonHistoryDao;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.work.software.partner.dao.PartnerDao;
import com.hnix.sd.work.software.partner.dto.*;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PartnerService {

    private final PartnerDao partnerDao;
    private final PartnerHistoryService partnerHistoryService;
    private final CommonHistoryDao commonHistoryDao;


    public List<PartnerInfoDto> getPartnerListAll() {
        return partnerDao.findAll()
                .stream()
                .map(this::convertToInfoDto)
                .collect(Collectors.toList());
    }

    public List<PartnerInfoWithNamesDto> searchPartnerWithNames(PartnerSearchKeywordDto keywordDto) {
        final String deptNm = checkSearchParameter( keywordDto.getDeptNm() );
        final String subCode = checkSearchParameter( keywordDto.getSubCode() );
        final String swName = checkSearchParameter( keywordDto.getSwName() );

        return partnerDao.findByDeptNmAndSubCodeAndSwName(deptNm, subCode, swName)
                .stream()
                .map(partner -> {
                    PartnerInfoWithNamesDto dto = new PartnerInfoWithNamesDto();
                    dto.setSubCode((String) partner[0]);
                    dto.setCompCode((String) partner[1]);
                    dto.setDeptNm((String) partner[2]);
                    dto.setSwCode((String) partner[3]);
                    dto.setSwName((String) partner[4]);
                    dto.setPartnerTypeCd((String) partner[5]);
                    dto.setCodeText((String) partner[6]);
                    dto.setPartnerContractCd((String) partner[7]);
                    dto.setPartnerContractCdText((String) partner[8]);
                    dto.setContractCancelPossibleYn((String) partner[9]);
                    dto.setUpgradePossibleYn((String) partner[10]);
                    dto.setTechnicalSupportYn((String) partner[11]);
                    dto.setVisitSupportYn((String) partner[12]);
                    dto.setRegularServiceCd((String) partner[13]);
                    dto.setRegularServiceCdText((String) partner[14]);
                    dto.setSubscriptionYn((String) partner[15]);
                    dto.setRemark((String) partner[16]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String checkSearchParameter(String str) {
        return StringUtils.isEmpty( str ) ? "" : str;
    }

    public PartnerInfoDto getPartnerInfoFromSubCode(PartnerCodeDto codeDto) {
        PartnerDto dto = partnerDao.findBySubCode(codeDto.getSubCode());
        if (dto == null) {
            return new PartnerInfoDto();
        }
        return convertToInfoDto(dto);
    }

    @Transactional
    public void updatePartnerInfo(PartnerInfoDto partnerInfo) {
        PartnerDto partner = partnerDao.findBySubCode(partnerInfo.getSubCode());
        boolean isNew = false;
        if (partner == null) {
            partner = new PartnerDto();
            isNew = true;
        }
        
        partnerHistoryService.addPartnerHistory(partner, partnerInfo);

        if ( isNew || StringUtils.isEmpty(partner.getRegId()) ) {
            partner.setRegId( partnerInfo.getRegId() );

            if ( StringUtils.isEmpty(partnerInfo.getSubCode()) ) {
                throw new BizException("Check Code");
            }

            partner.setSubCode( partnerInfo.getSubCode() );
            partner.setRegDt( LocalDateTime.now() );
        } else {
            partner.setModId( partnerInfo.getModId() );
            partner.setModDt( LocalDateTime.now() );
        }

        partner.setSwCode( partnerInfo.getSwCode() );
        partner.setCompCode( partnerInfo.getCompCode() );
        partner.setPartnerTypeCd( partnerInfo.getPartnerTypeCd() );
        partner.setPartnerContractCd( partnerInfo.getPartnerContractCd() );
        partner.setContractCancelPossibleYn( partnerInfo.getContractCancelPossibleYn() );
        partner.setUpgradePossibleYn( partnerInfo.getUpgradePossibleYn() );
        partner.setTechnicalSupportYn( partnerInfo.getTechnicalSupportYn() );
        partner.setVisitSupportYn( partnerInfo.getVisitSupportYn() );
        partner.setRegularServiceCd( partnerInfo.getRegularServiceCd() );
        partner.setSubscriptionYn( partnerInfo.getSubscriptionYn() );
        partner.setRemark( partnerInfo.getRemark() );

        if (isNew) {
            partnerDao.insertPartner(partner);
        } else {
            partnerDao.updatePartner(partner);
        }
    }

    public void removePartnerInfo(PartnerRemoveDto removeDto) {
        partnerDao.deletePartner(removeDto.getSubCode());
        commonHistoryDao.deleteByTargetIdAndMenuCd(removeDto.getSubCode(), "work-software-company");
    }

    public Integer findSubCodeSeqWithSwCode(String swCode) {
        return partnerDao.countBySubCodeFromSwCode(swCode);
    }

    public Boolean checkExistBySubCode(String subCode) {
        return partnerDao.findBySubCode(subCode) != null;
    }

    private PartnerInfoDto convertToInfoDto(PartnerDto partner) {
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
