package com.hnix.sd.work.registration.certificate.dao;

import com.hnix.sd.work.registration.certificate.dto.CertificateWithUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CertificateDao {

    CertificateWithUserDto findCertificateByServiceNo(String serviceNo);
    
    void insertCertificate(CertificateWithUserDto certificate);
    
    void updateCertificate(CertificateWithUserDto certificate);
    
    void saveAll(@Param("certificates") List<CertificateWithUserDto> certificates);
    
    default CertificateWithUserDto save(CertificateWithUserDto dto) {
        if (findCertificateByServiceNo(dto.getServiceNo()) == null) {
            insertCertificate(dto);
        } else {
            updateCertificate(dto);
        }
        return dto;
    }
}
