package com.hnix.sd.core.utils;


import java.util.ArrayList;
import java.util.List;

public interface GenericMapper<DTO, Entity> {

    DTO toDTO(Entity entity);

    Entity toEntity(DTO dto);

    ArrayList<DTO> toDtoList(List<Entity> list);

    ArrayList<Entity> toEntityList(List<DTO> dtoList);

    void findByLoginReqDto(DTO dto, Entity entity);

}
