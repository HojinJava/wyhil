package com.hnix.sd.work.software.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareExcelDto {
  private String swCode;
	private String swName;
	private String swDesc;

	private String regId;
	private String modId;


	private boolean newRow;        // 신규 여부
	private boolean updateRow;     // 수정 여부
	private String errorMsg;       // 검증 오류 메시지

	public SoftwareExcelDto(String swCode, String swName, String swDesc) {
    this.swCode = swCode;
    this.swName = swName;
    this.swDesc = swDesc;
	}

  public String validate() {
		String msg = "";

		if(swCode == null || swCode.trim().isEmpty()) {
			msg +="소프트웨어 CODE가 존재하지 않습니다. / ";
		}
		if(swName == null || swName.trim().isEmpty()) {
			msg +="소프트웨어 이름이 존재하지 않습니다. / ";
		}

		return msg;
	}
}
