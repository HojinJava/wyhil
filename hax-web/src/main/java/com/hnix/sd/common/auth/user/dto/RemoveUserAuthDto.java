package com.hnix.sd.common.auth.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RemoveUserAuthDto {

    private List<UserAuthIdsDto> userAuthIds;

}
