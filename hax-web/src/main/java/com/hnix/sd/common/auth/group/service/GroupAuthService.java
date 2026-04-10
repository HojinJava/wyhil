package com.hnix.sd.common.auth.group.service;

import com.hnix.sd.common.auth.group.dao.GroupAuthDao;
import com.hnix.sd.common.auth.group.dto.GroupAuthDto;
import com.hnix.sd.common.auth.group.dto.GroupAuthInfoDto;
import com.hnix.sd.core.exception.BizException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupAuthService {

    private final GroupAuthDao groupAuthDao;

    private GroupAuthInfoDto toInfoDto(GroupAuthDto dto) {
        if (dto == null) return new GroupAuthInfoDto();
        return new GroupAuthInfoDto(
            dto.getGroupCd(),
            dto.getGroupNm(),
            dto.getGroupDesc(),
            dto.getRegId(),
            dto.getModId()
        );
    }

    private GroupAuthDto toDto(GroupAuthInfoDto infoDto) {
        if (infoDto == null) return new GroupAuthDto();
        GroupAuthDto dto = new GroupAuthDto();
        dto.setGroupCd(infoDto.getGroupCd());
        dto.setGroupNm(infoDto.getGroupNm());
        dto.setGroupDesc(infoDto.getGroupDesc());
        dto.setRegId(infoDto.getRegId());
        dto.setModId(infoDto.getModId());
        return dto;
    }

    public List<GroupAuthInfoDto> getGroupAuthList() {
        return groupAuthDao.findAll()
                .stream()
                .map(this::toInfoDto)
                .collect(Collectors.toList());
    }

    public GroupAuthInfoDto getGroupAuthFromGroupCd(String groupCd) {
        return groupAuthDao.findByGroupCd(groupCd)
                .map(this::toInfoDto)
                .orElseGet(GroupAuthInfoDto::new);
    }

    @Transactional
    public String storeGroupAuth(GroupAuthInfoDto groupAuthInfoDto) {
        if ( StringUtils.isEmpty(groupAuthInfoDto.getGroupCd()) ||
                StringUtils.isEmpty(groupAuthInfoDto.getGroupNm()) ) {
            return "FAILED";
        }

        GroupAuthDto existingDto = groupAuthDao.findByGroupCd(groupAuthInfoDto.getGroupCd()).orElse(null);

        if (existingDto == null) {
            if ( StringUtils.isEmpty( groupAuthInfoDto.getGroupCd() )) {
                throw new BizException("그룹 코드 값이 없습니다.");
            }
            GroupAuthDto newDto = toDto(groupAuthInfoDto);
            groupAuthDao.insertGroupAuthDto(newDto);
        } else {
            existingDto.setGroupNm(groupAuthInfoDto.getGroupNm());
            existingDto.setGroupDesc(groupAuthInfoDto.getGroupDesc());
            existingDto.setModId(groupAuthInfoDto.getModId());
            groupAuthDao.updateGroupAuthDto(existingDto);
        }

        return "SUCCESS";
    }

    public void removeGroupAuth(String groupCd) {
        groupAuthDao.deleteByGroupCd(groupCd);
    }

    public Boolean checkDuplicateAuthCode(String authCode) {
        return groupAuthDao.existsByGroupCd(authCode);
    }

}
