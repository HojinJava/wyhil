package com.hnix.sd.common.department.dto.manage;

import com.hnix.sd.common.department.dto.manage.DeptRegisterDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultipleRegisterDto {

    private List<DeptRegisterDto> deptRegisterList;

}
