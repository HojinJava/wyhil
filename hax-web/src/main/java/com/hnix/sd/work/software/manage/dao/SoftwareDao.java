package com.hnix.sd.work.software.manage.dao;

import com.hnix.sd.work.software.manage.dto.SoftwareDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SoftwareDao {

    List<SoftwareDto> findAll();

    List<SoftwareDto> findBySwCodeAndSwName(@Param("swCode") String swCode, @Param("swName") String swName);

    SoftwareDto findBySwCode(@Param("swCode") String swCode);

    boolean existsBySwCode(@Param("swCode") String swCode);

    void insertSoftware(SoftwareDto software);

    void updateSoftware(SoftwareDto software);

    void deleteBySwCode(@Param("swCode") String swCode);
}

