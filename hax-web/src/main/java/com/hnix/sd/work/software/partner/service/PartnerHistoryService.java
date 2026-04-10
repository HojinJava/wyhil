package com.hnix.sd.work.software.partner.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.hnix.sd.common.code.dto.SubCodeDto;
import com.hnix.sd.common.code.service.CodeService;
import com.hnix.sd.common.department.dto.DepartmentDto;
import com.hnix.sd.common.history.service.CommonHistoryService;
import com.hnix.sd.common.history.CommonHistoryUtil;
import com.hnix.sd.common.history.dto.CommonHistoryDto;
import com.hnix.sd.common.department.dao.DepartmentDao;
import com.hnix.sd.work.software.partner.dto.PartnerDto;
import com.hnix.sd.work.software.partner.dto.PartnerInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PartnerHistoryService {
  private final CommonHistoryService commonHistoryService;
  private final CodeService codeService;
  private final DepartmentDao departmentDao;

  public void addPartnerHistory(PartnerDto partner, PartnerInfoDto partnerInfoDto) {
    String menuCd = "work-software-company";
    String hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_UPDATE;
    if(StringUtils.isEmpty(partner.getRegId())) {
      hisType = CommonHistoryUtil.COMMON_HISTORY_TYPE_CREATE;
    }

    String msg = "";

    if(CommonHistoryUtil.isOtherValue(partner.getCompCode(), partnerInfoDto.getCompCode())) {
      List<Object[]> compCodeList = departmentDao.findDeptWithCompanyName();
      Map<String, String> compCodeMap = compCodeList.stream()
          .collect(Collectors.toMap(
              row -> ((DepartmentDto) row[1]).getDeptCd(),
              row -> ((DepartmentDto) row[1]).getDeptNm() 
          ));

      String oldCompCodeName = compCodeMap.get(partner.getCompCode());
      String newCompCodeName = compCodeMap.get(partnerInfoDto.getCompCode());
      msg = CommonHistoryUtil.getCommonHistory(msg, "협력사", oldCompCodeName, newCompCodeName);
    }

    if(CommonHistoryUtil.isOtherValue(partner.getPartnerContractCd(), partnerInfoDto.getPartnerContractCd())) {
      List<SubCodeDto> partnerContractList = codeService.getSubCodeFromGroupCodeCd("COMP_CONTRACT");
      Map<String, String> partnerContractMap = partnerContractList.stream()
        .collect(Collectors.toMap(SubCodeDto::getCodeCd, SubCodeDto::getCodeText));

      String oldPartnerContractName = partnerContractMap.get(partner.getPartnerContractCd());
      String newPartnerContractName = partnerContractMap.get(partnerInfoDto.getPartnerContractCd());
      msg = CommonHistoryUtil.getCommonHistory(msg, "계약관계", oldPartnerContractName, newPartnerContractName);
    }

    if(CommonHistoryUtil.isOtherValue(partner.getPartnerTypeCd(), partnerInfoDto.getPartnerTypeCd())) {
      List<SubCodeDto> partnerTypeList = codeService.getSubCodeFromGroupCodeCd("PARTNER_TYPE");
      Map<String, String> partnerTypeMap = partnerTypeList.stream()
        .collect(Collectors.toMap(SubCodeDto::getCodeCd, SubCodeDto::getCodeText));

      String oldPartnerTypeName = partnerTypeMap.get(partner.getPartnerTypeCd());
      String newPartnerTypeName = partnerTypeMap.get(partnerInfoDto.getPartnerTypeCd());
      msg = CommonHistoryUtil.getCommonHistory(msg, "계약구분", oldPartnerTypeName, newPartnerTypeName);
    }

    if(CommonHistoryUtil.isOtherValue(partner.getRegularServiceCd(), partnerInfoDto.getRegularServiceCd())) {
      List<SubCodeDto> regularServiceList = codeService.getSubCodeFromGroupCodeCd("REGULAR_SERVICE");
      Map<String, String> regularServiceMap = regularServiceList.stream()
        .collect(Collectors.toMap(SubCodeDto::getCodeCd, SubCodeDto::getCodeText));

      String oldRegularServiceName = regularServiceMap.get(partner.getRegularServiceCd());
      String newRegularServiceName = regularServiceMap.get(partnerInfoDto.getRegularServiceCd());
      msg = CommonHistoryUtil.getCommonHistory(msg, "정기점검", oldRegularServiceName, newRegularServiceName);
    }

    if (CommonHistoryUtil.isOtherValue(partner.getRemark(), partnerInfoDto.getRemark())) {
      msg = CommonHistoryUtil.getCommonHistory(msg, "비고", partner.getRemark(),partnerInfoDto.getRemark());
    }

    if (CommonHistoryUtil.isOtherValue(partner.getContractCancelPossibleYn(), partnerInfoDto.getContractCancelPossibleYn())) {
      msg = CommonHistoryUtil.getCommonHistory(msg, "중도계약해지 가능 여부", partner.getContractCancelPossibleYn(), partnerInfoDto.getContractCancelPossibleYn());
    }

    if (CommonHistoryUtil.isOtherValue(partner.getUpgradePossibleYn(), partnerInfoDto.getUpgradePossibleYn())) {
      msg = CommonHistoryUtil.getCommonHistory(msg, "업그레이드 가능 여부", partner.getUpgradePossibleYn(), partnerInfoDto.getUpgradePossibleYn());
    }

    if (CommonHistoryUtil.isOtherValue(partner.getTechnicalSupportYn(), partnerInfoDto.getTechnicalSupportYn())) {
      msg = CommonHistoryUtil.getCommonHistory(msg, "기술지원 수행 여부", partner.getTechnicalSupportYn(), partnerInfoDto.getTechnicalSupportYn());
    }

    if (CommonHistoryUtil.isOtherValue(partner.getVisitSupportYn(), partnerInfoDto.getVisitSupportYn())) {
      msg = CommonHistoryUtil.getCommonHistory(msg, "방문지원 여부", partner.getVisitSupportYn(), partnerInfoDto.getVisitSupportYn());
    }

    if (CommonHistoryUtil.isOtherValue(partner.getSubscriptionYn(), partnerInfoDto.getSubscriptionYn())) {
      msg = CommonHistoryUtil.getCommonHistory(msg, "Subscription 여부", partner.getSubscriptionYn(), partnerInfoDto.getSubscriptionYn());
    }

    CommonHistoryDto commonHistoryDto = new CommonHistoryDto();
    commonHistoryDto.setTargetId(partnerInfoDto.getSubCode());
    commonHistoryDto.setMenuCd(menuCd);
    commonHistoryDto.setHisTypeCd(hisType);
    commonHistoryDto.setHisContents(msg);
    commonHistoryDto.setUserId(partnerInfoDto.getRegId());
    commonHistoryDto.setFileId(partnerInfoDto.getFileId());

    commonHistoryService.addCommonHistory(commonHistoryDto);
  }
}
