package com.hnix.sd.common.mail.dao;

import com.hnix.sd.common.mail.dto.MailDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MailDao {

    MailDto findByCompCdAndMailTypeCdEntity(@Param("compCd") String compCd, @Param("mailTypeCd") String mailTypeCd);

    default Optional<MailDto> findByCompCdAndMailTypeCd(String compCd, String mailTypeCd) {
        return Optional.ofNullable(findByCompCdAndMailTypeCdEntity(compCd, mailTypeCd));
    }

    MailDto findByUserDeptCdAndMailTypeCdEntity(@Param("deptCd") String deptCd, @Param("mailTypeCd") String mailTypeCd);

    default Optional<MailDto> findByUserDeptCdAndMailTypeCd(String deptCd, String mailTypeCd) {
        return Optional.ofNullable(findByUserDeptCdAndMailTypeCdEntity(deptCd, mailTypeCd));
    }

    List<Object[]> findByCompCd(@Param("compCd") String compCd);

    void insertMail(MailDto mail);

    void updateMail(MailDto mail);

    default void save(MailDto mail) {
        updateMail(mail);
    }

    void deleteMail(@Param("compCd") String compCd, @Param("mailTypeCd") String mailTypeCd);

    default void delete(MailDto mail) {
        deleteMail(mail.getCompCd(), mail.getMailTypeCd());
    }
}
