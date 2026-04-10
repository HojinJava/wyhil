package com.hnix.sd.work.software.contract.dao;

import com.hnix.sd.work.software.contract.dto.ContractSubDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContractSubDao {

    ContractSubDto findByContractNo(@Param("contractNo") String contractNo);

    void insertByContractNo(@Param("contractNo") String contractNo);

}
