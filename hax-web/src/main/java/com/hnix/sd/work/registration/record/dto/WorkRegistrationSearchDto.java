package com.hnix.sd.work.registration.record.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class WorkRegistrationSearchDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private String customerCd;
    private String departmentCd;
    private String partnerCd;
    private String satisfied;
    private String softwareName;
    private String subCode;
    
    // Auth & Context
    private String loginUserId;
    private String loginUserCompanyCd;
    private List<String> userAuths;
    private boolean admin;
    private boolean manage;
    private boolean deptManage;

    // Pagination
    private Integer pageNo = 0;
    private Integer pageSize = 10;
    private String criteria = "regDt";
    private String sort = "DESC";

    public int getOffset() {
        return (this.pageNo != null ? this.pageNo : 0) * (this.pageSize != null ? this.pageSize : 10);
    }
}
