package com.hnix.sd.work.registration.survey.service;

import com.hnix.sd.common.code.dao.CodeDao;
import com.hnix.sd.common.code.dto.GroupCodeAllDto;
import com.hnix.sd.common.user.service.UserCertService;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.EncryptionUtils;
import com.hnix.sd.work.registration.certificate.dao.CertificateDao;
import com.hnix.sd.work.registration.certificate.dto.CertificateWithUserDto;
import com.hnix.sd.work.registration.certificate.dto.StoreCertificateDto;
import com.hnix.sd.work.registration.record.dto.FindWorkRegistrationDto;
import com.hnix.sd.work.registration.record.dto.RequesterUserIdDto;
import com.hnix.sd.work.registration.record.dao.WorkRegistrationDao;
import com.hnix.sd.work.registration.survey.dao.SurveyDao;
import com.hnix.sd.work.registration.survey.dto.StoreSurveyDto;
import com.hnix.sd.work.registration.survey.dto.FindWorkRegisterWithSurvey;
import com.hnix.sd.work.registration.survey.dto.SurveyInfoDto;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class SurveyService {

    private final SurveyDao surveyDao;
    private final CertificateDao certificateDao;
    private final WorkRegistrationDao workDao;
    private final CodeDao codeDao;

    private final UserCertService userCertService;

    // TODO: Define a proper Code class or CodeDto if Code entity is removed.
    // private List<Code> codeElements;


    /* 작업 등록 (Work Registration) 시, 추가되는 설문조사 데이터 */
    public void createSurveyFormWork(List<RequesterUserIdDto> requester) {
        if (requester == null || requester.isEmpty()) return;

        List<SurveyInfoDto> entities = new ArrayList<>();

        for (var req : requester) {
            SurveyInfoDto survey = new SurveyInfoDto();
            survey.setServiceNo( req.getServiceNo() );
            survey.setUserId( req.getUserId() );
            survey.setPointQui(0);
            survey.setPointAcc(0);
            survey.setPointFri(0);
            survey.setPointSat(0);

            entities.add(survey);
        }
        surveyDao.saveAll(entities);
    }

    /* 고객사 설문조사 화면에서 전달받은 데이터 */
    public void storeCustomerSurvey(StoreSurveyDto storeSurveyDto) throws NotFoundException {
        if ( checkEmpty(storeSurveyDto.getServiceNo()) || checkEmpty(storeSurveyDto.getUserId()) ) {
            throw new NotFoundException("사용자 ID 또는 서비스 ID를 확인해주세요.");
        }
        LocalDateTime current = LocalDateTime.now();

        SurveyInfoDto entity = new SurveyInfoDto();
        entity.setServiceNo(storeSurveyDto.getServiceNo());
        entity.setUserId(storeSurveyDto.getUserId());
        entity.setPointQui(storeSurveyDto.getPointQui());
        entity.setPointAcc(storeSurveyDto.getPointAcc());
        entity.setPointFri(storeSurveyDto.getPointFri());
        entity.setPointSat(storeSurveyDto.getPointSat());
        entity.setSurveyDt(current);

        surveyDao.save(entity);
    }

    public void storeCustomerCertificate(StoreCertificateDto storeCertDto) throws NotFoundException {
        if ( checkEmpty(storeCertDto.getServiceNo()) || checkEmpty(storeCertDto.getUserId()) ) {
            throw new NotFoundException("사용자 ID 또는 서비스 ID를 확인해주세요.");
        }
        LocalDateTime current = LocalDateTime.now();

        CertificateWithUserDto entity = new CertificateWithUserDto();
        entity.setServiceNo(storeCertDto.getServiceNo());
        entity.setUserId(storeCertDto.getUserId());
        entity.setCertComment(storeCertDto.getCertComment());
        entity.setCertDt(current);

        certificateDao.save(entity);
    }

    private boolean checkEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public SurveyInfoDto getSurveyByServiceNo(String serviceNo) {
        SurveyInfoDto entity = surveyDao.findById(serviceNo);
        return entity == null ? new SurveyInfoDto() : entity;
    }

    public boolean checkSurveyByServiceNo(String serviceNo) {
        SurveyInfoDto entity = surveyDao.findById(serviceNo);
        if (entity == null) return false;
        return !StringUtils.isEmpty(entity.getUserId()) && !Objects.isNull(entity.getSurveyDt());
    }


    //설문조사 화면 데이터 호출
    @Transactional
    public FindWorkRegisterWithSurvey getWorkRegistrationFromServiceNo(String serviceLink) throws NotFoundException {

        System.out.println("# [SurveyService.java] getWorkRegistrationFromServiceNo() start");

        String urlQuery = EncryptionUtils.decrypt( serviceLink );

        String serviceNo = Arrays.stream(urlQuery.split("&"))
                .filter(param -> param.startsWith("serviceNo"))
                .findFirst().map(param -> param.split("=")[1])
                .orElse("");

        if (StringUtils.isEmpty(serviceNo)) {
            throw new BizException("서비스 번호가 존재하지 않습니다.");
        }

        FindWorkRegisterWithSurvey serviceInfo = new FindWorkRegisterWithSurvey();

        List<GroupCodeAllDto> codeElements = codeDao.findAll();

        FindWorkRegistrationDto workRegistration = workDao.findWorkRegistrationByServiceNo(serviceNo);
        if (workRegistration == null) {
            throw new NotFoundException(String.format("'%s' 서비스 번호로 등록된 작업이 없습니다.", serviceNo));
        }

        CertificateWithUserDto certificate = certificateDao.findCertificateByServiceNo(serviceNo);
        if (certificate == null) {
            throw new NotFoundException(String.format("'%s' 서비스 번호로 등록된 승인 내역이 없습니다.", serviceNo));
        }

        SurveyInfoDto survey = getSurveyByServiceNo(serviceNo);

        workRegistration.setReqUserContractType( findCodeTextFromCodeId(workRegistration.getReqUserContractType(), codeElements) );
        workRegistration.setServiceCdNm( findCodeDescFromCodeId(workRegistration.getServiceCd(), codeElements) );
        workRegistration.setReqSupportCdNm( findCodeDescFromCodeId(workRegistration.getReqSupportCd(), codeElements) );
        workRegistration.setProcSupportCdNm( findCodeDescFromCodeId(workRegistration.getProcSupportCd(), codeElements) );
        workRegistration.setStatusCd( findCodeDescFromCodeId(workRegistration.getStatusCd(), codeElements) );

        serviceInfo.setRegistration( workRegistration );
        serviceInfo.setCertificate( certificate );
        serviceInfo.setSurvey( survey );

        System.out.println("# [SurveyService.java] getWorkRegistrationFromServiceNo() end");

        return serviceInfo;
    }


    private String findCodeDescFromCodeId(String subCodeId, List<GroupCodeAllDto> codeElements) {
        if (codeElements == null) return "";
        return codeElements.stream()
                .filter(c -> c.getCodeCd().equals(subCodeId))
                .findFirst()
                .map(GroupCodeAllDto::getCodeDesc)
                .orElse("");
    }

    private String findCodeTextFromCodeId(String subCodeId, List<GroupCodeAllDto> codeElements) {
        if (codeElements == null) return "";
        return codeElements.stream()
                .filter(c -> c.getCodeCd().equals(subCodeId))
                .findFirst()
                .map(GroupCodeAllDto::getCodeText)
                .orElse("");
    }


    public boolean checkConsentUsedLink(String serviceLink) {
        String userId = findUserIdFromServiceLink(serviceLink);
        return userCertService.checkUserConsent(userId);
    }

    public void consentToPersonalInformation(String serviceLink) {
        String userId = findUserIdFromServiceLink(serviceLink);

        if ( StringUtils.isEmpty(userId) ) {
            throw new BizException("사용자 ID가 존재하지 않습니다.");
        }
        userCertService.updatedUserConsentStatus(userId);
    }


    private String findUserIdFromServiceLink(String link) {
        String urlQuery = EncryptionUtils.decrypt( link );
        String userId = "";

        for (var param : urlQuery.split("&")) {
            if (param.startsWith("userId")) {
                userId = param.split("=")[1];
                break;
            }
        }
        return userId;
    }

}
