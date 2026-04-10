package com.hnix.sd.common.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;

    private Integer idx;

    private String userNm;

    private String userEmail;

    private String sendYn;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private LocalDateTime sendDt;

    private String companyCd;

    public String getUserSendYn() {
        return sendYn != null ? sendYn :"N";
    }
}
