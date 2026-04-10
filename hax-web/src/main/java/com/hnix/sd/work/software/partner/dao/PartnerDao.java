package com.hnix.sd.work.software.partner.dao;

import com.hnix.sd.work.software.partner.dto.PartnerDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PartnerDao {

    List<PartnerDto> findAll();

    PartnerDto findBySubCode(@Param("subCode") String subCode);

    boolean existsBySubCode(@Param("subCode") String subCode);

    List<Object[]> findByDeptNmAndSubCodeAndSwName(@Param("deptNm") String deptNm,
                                                   @Param("subCode") String subCode,
                                                   @Param("swName") String swName);

    List<PartnerDto> findExistingPartnerBySoftwareCode(@Param("swCode") String swCode);

    Integer countBySubCodeFromSwCode(@Param("swCode") String swCode);

    void insertPartner(PartnerDto partner);

    void updatePartner(PartnerDto partner);

    void deletePartner(@Param("subCode") String subCode);
}
