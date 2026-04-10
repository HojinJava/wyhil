package com.hnix.sd.common.department.dto.manage;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptExcelDownloadRequest {
	private List<String> deptCd;
}
