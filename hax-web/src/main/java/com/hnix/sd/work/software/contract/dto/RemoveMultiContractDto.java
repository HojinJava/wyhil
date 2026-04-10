package com.hnix.sd.work.software.contract.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RemoveMultiContractDto {

    private Set<String> contractIds;
    private String userId;

}
