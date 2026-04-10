package com.hnix.sd.work.registration.certificate.service;

import com.hnix.sd.common.user.service.UserCertService;
import com.hnix.sd.core.utils.EncryptionUtils;
import com.hnix.sd.work.registration.certificate.dao.CertificateDao;
import com.hnix.sd.work.registration.certificate.dto.CertificateWithUserDto;
import com.hnix.sd.work.registration.record.dto.RequesterUserIdDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CertificateService {

    private final CertificateDao certificateDao;
    private final UserCertService userCertService;


    /* 작업 등록 (Work Registration) 시, 추가되는 승인/반려 데이터 */
    public void createCertificateFormWork(List<RequesterUserIdDto> requester) {
        if (requester == null || requester.isEmpty()) return;

        List<CertificateWithUserDto> entities = new ArrayList<>();

        for (var req : requester) {
            CertificateWithUserDto cert = new CertificateWithUserDto();
            cert.setServiceNo( req.getServiceNo() );
            cert.setUserId( req.getUserId() );
            cert.setCertCd("WORK_STATUS_SAVE"); // 임시저장 상태로 승인 메일을 전송 전 코드

            entities.add(cert);
        }
        certificateDao.saveAll(entities);
    }


    public CertificateWithUserDto getCertificateByServiceNo(String serviceNo) throws NotFoundException {
        CertificateWithUserDto result = certificateDao.findCertificateByServiceNo(serviceNo);
        if (result == null) {
            throw new NotFoundException(String.format("'%s' 서비스 번호로 등록된 승인 내역이 없습니다.", serviceNo));
        }
        return result;
    }

    public boolean consentToPersonalInformation(String link, String modId) {
        String email = findEmailFromCertificateLink(link);

        if ( StringUtils.isEmpty(email) ) {
            return false;
        }
        userCertService.updatedUserCertificateStatus(email, modId);
        return true;
    }

    private String findEmailFromCertificateLink(String link) {
        String urlQuery = EncryptionUtils.decrypt( link );
        String email = "";

        for (var param : urlQuery.split("&")) {
            if (param.startsWith("userEmail")) {
                email = param.split("=")[1];
                break;
            }
        }
        return email;
    }

}
