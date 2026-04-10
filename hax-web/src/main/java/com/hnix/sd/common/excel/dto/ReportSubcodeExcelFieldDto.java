package com.hnix.sd.common.excel.dto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ReportSubcodeExcelFieldDto {
  private Map<Integer, Object> cellData;
}
