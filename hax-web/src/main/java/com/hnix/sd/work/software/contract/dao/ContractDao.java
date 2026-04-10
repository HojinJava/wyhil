package com.hnix.sd.work.software.contract.dao;

import com.hnix.sd.work.software.contract.dto.ContractDetailDto;
import com.hnix.sd.work.software.contract.dto.ContractDto;
import com.hnix.sd.work.software.contract.dto.ContractGridInfoDto;
import com.hnix.sd.work.software.contract.dto.ContractSearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface ContractDao {

    Optional<ContractDto> findById(@Param("id") String contractNo);

    ContractDto findByContractNo(@Param("contractNo") String contractNo);

    Set<String> findByContractNoStartingWith(@Param("prefix") String contractPrefix);

    void updateContractDeleteYn(@Param("contractNo") String contractNo);

    void updateMultiContractDeleteYn(@Param("contractIds") Collection<String> contractIds);

    List<ContractDto> findAllByContractNos(@Param("contractIds") Collection<String> contractIds);

    void insertContract(ContractDto contract);

    void updateContract(ContractDto contract);

    List<ContractGridInfoDto> findContractPagination(ContractSearchDto searchDto);

    long countContractPagination(ContractSearchDto searchDto);

    List<ContractGridInfoDto> findContractDialogGrid(Map<String, Object> params);

    ContractDetailDto findContractDetailByContractNo(@Param("contractNo") String contractNo);
}
