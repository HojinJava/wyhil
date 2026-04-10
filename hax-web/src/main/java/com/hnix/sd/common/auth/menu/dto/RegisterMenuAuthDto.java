package com.hnix.sd.common.auth.menu.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterMenuAuthDto {

    private List<UpdateMenuAuthInfoDto> menuAuthList;

}
