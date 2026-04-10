package com.hnix.sd.common.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRemoveDto {

    private List<String> userIds;

}
