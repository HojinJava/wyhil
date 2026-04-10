package com.hnix.sd.common.code.service;

import com.hnix.sd.common.code.dto.GroupCodeAllDto;
import com.hnix.sd.common.code.dto.GroupCodeDto;
import com.hnix.sd.common.code.dto.MultiGroupCodeIdDto;
import com.hnix.sd.common.code.dto.SubCodeDto;
import com.hnix.sd.common.code.dto.CodeDto;
import com.hnix.sd.common.code.dao.CodeDao;
import com.hnix.sd.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CodeService {

    private final CodeDao codeDao;

    private String GROUP_SUBCODE = "*";

    public List<GroupCodeAllDto> getGroupCodeListAll() {
        return codeDao.findAll();
    }

    public List<GroupCodeDto> getGroupCodeList() {
        List<GroupCodeDto> result = new ArrayList<>();

        result = codeDao.findBySubCodeCd(GROUP_SUBCODE);

        return result;
    }

    public List<SubCodeDto> getSubCodeFromGroupCodeCd(String groupCodeId) {
        return codeDao.findByCodeGroupCd(groupCodeId);
    }

    public String getSubCodeNameByCodeVal(String codeGroupCd, String codeVal) {
        String returnStr = "";
        List<SubCodeDto> filterList = getSubCodeFromGroupCodeCd(codeGroupCd)
                                        .stream()
                                        .filter(code -> code.getCodeVal().equals(codeVal))
                                        .collect(Collectors.toList());

        if(filterList != null && filterList.size() > 0) {
            returnStr = filterList.get(0).getCodeText();
        }

        return returnStr;   
    }

    public List<SubCodeDto> getSubCodeFromMultiGroupCodeId(MultiGroupCodeIdDto multiGroupCodeIdDto) {
        return codeDao.findByMultiCodeGroupCd(multiGroupCodeIdDto.getIds());
    }

    public GroupCodeAllDto storeGroupCode(String userId,
                                          GroupCodeAllDto groupCode,
                                          List<SubCodeDto> subCodeList) {
        if (userId == null) {
            throw new BizException("loginFail");
        }

        /* 등록된 사용자인지 확인하는 로직 필요. */
        if (!userId.equals("admin")) {
            throw new BizException("userNotFound");
        }

        if (groupCode.getCodeGroupCd() == null || groupCode.getCodeGroupCd().isBlank()) {
            throw new BizException("그룹코드를 입력해주세요.");
        }

        CodeDto code = codeDao
                .findByCodeId(groupCode.getCodeGroupCd(), GROUP_SUBCODE)
                .orElseGet(CodeDto::new);

        /* INSERT & UPDATE 구분 */
        if (code.getCodeCd() == null || code.getGroupCd() == null) {
            code.setGroupCd(groupCode.getCodeGroupCd());
            code.setCodeCd(GROUP_SUBCODE);
            code.setUseYn('Y');
            code.setCodeDesc(groupCode.getCodeDesc());
            code.setRegId(userId);
            code.setRegDt(LocalDateTime.now());
            codeDao.insert(code);
        } else {
            code.setCodeDesc(groupCode.getCodeDesc());
            code.setUseYn(groupCode.getUseYn());
            code.setCodeText(groupCode.getCodeText());
            code.setModId(userId);
            code.setModDt(LocalDateTime.now());
            codeDao.update(code);
        }

        if (!subCodeList.isEmpty()) storeSubCode(userId, groupCode.getCodeGroupCd(), subCodeList);

        GroupCodeAllDto result = new GroupCodeAllDto();
        result.setCodeGroupCd(code.getGroupCd());
        result.setCodeCd(code.getCodeCd());
        result.setCodeText(code.getCodeText());
        result.setCodeDesc(code.getCodeDesc());
        result.setUseYn(code.getUseYn());
        return result;
    }

    public List<SubCodeDto> storeSubCode(String userId, String groupCodeCd, List<SubCodeDto> subCodeDto) {
        if (groupCodeCd == null || groupCodeCd.isBlank()) {
            throw new BizException("그룹 코드를 입력해주세요.");
        }

        subCodeDto.forEach(sub -> {
            if (sub.getCodeCd() == null || sub.getCodeCd().isBlank()) {
                throw new BizException("서브코드를 입력해주세요.");
            }

            CodeDto dto = CodeDto.builder()
                .groupCd(groupCodeCd)
                .codeCd(sub.getCodeCd())
                .codeSeq(sub.getCodeSeq())
                .useYn(sub.getUseYn())
                .codeText(sub.getCodeText())
                .codeDesc(sub.getCodeDesc())
                .codeVal(sub.getCodeVal())
                .regId(userId)
                .regDt(LocalDateTime.now())
                .build();

            if (codeDao.findByCodeId(groupCodeCd, sub.getCodeCd()).isPresent()) {
                codeDao.update(dto);
            } else {
                codeDao.insert(dto);
            }
        });

        return codeDao.findByCodeGroupCd(groupCodeCd);
    }

    public void removeGroupCode(String groupCodeCd) {
        codeDao.deleteByGroupCode(groupCodeCd);
    }

    public void removeSubCode(String groupCodeCd, List<String> subCodeCdList) {
        codeDao.deleteBySubCode(groupCodeCd, subCodeCdList);
    }

}
