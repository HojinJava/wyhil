package com.hnix.sd.common.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSearchPageDto {

    private String deptCd;
    private String userNm;
    private Integer pageNo = 0;
    private Integer pageSize = 10;
    private String criteria = "regDt";
    private String sort = "DESC";
    private String userEmail;
    private long offset;
    private int limit;
    private String direction;
    private String searchType;
    private String searchValue;
}
