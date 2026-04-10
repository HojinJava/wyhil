package com.hnix.sd.work.registration.record.dto;

import com.hnix.sd.common.user.dto.UserRegistDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationResultDto {

    private List<UserRegistDto> failed;
    private List<RequesterUserIdDto> created;

}
