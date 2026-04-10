package com.hnix.sd.common.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;
    private String company;
    private String deptNm;

    @NotNull
    private String deptCd;

    @NonNull
    private String userNm;

    private String userDeptNm;

    private String userPw;

    private String userPositionNm;

    private String userEmail;

    private String userPhoneOffice;

    private String userPhoneMobile;

    private Character deleteYn;

    private String userCheckCd;

    private String remark;

    private Character userCertYn;

    private Character userConsentYn;

    private String regId;

    private String modId;

    private Character userPwResetYn;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime regDt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime modDt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime userConsentDt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime userCertDt;

    public Character getUserCertYn() {
        return userCertYn != null ? userCertYn : 'N';
    }

    public Character getUserConsentYn() {
        return userConsentYn != null ? userConsentYn : 'N';
    }

    public Character getUserPwResetYn() {
        return userPwResetYn != null ? userPwResetYn : 'N';
    }

    public Character getDeleteYn() {
        return deleteYn != null ? deleteYn : 'N';
    }

}
