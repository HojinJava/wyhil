package com.hnix.sd.common.department.dto.manage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptRemoveDto {

    private List<String> deptCds;

}
