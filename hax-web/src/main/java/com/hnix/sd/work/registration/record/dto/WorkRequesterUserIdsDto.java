package com.hnix.sd.work.registration.record.dto;

import com.hnix.sd.common.user.dto.UserRegistDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkRequesterUserIdsDto {

    private List<UserRegistDto> requester;

}
