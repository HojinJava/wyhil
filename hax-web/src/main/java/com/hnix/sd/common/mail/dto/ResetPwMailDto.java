package com.hnix.sd.common.mail.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ResetPwMailDto {

    @Schema(title = "받는 사람", example = "user@naver.com")
    private String to;
    @Schema(title = "참조", example = "sub_user@naver.com")
    private String cc;
    @Schema(title = "제목", example = "비밀번호 변경 안내")
    private String subject;
    @Schema(title = "내용", example = "비밀번호가 아래와 같이 변경되었습니다.")
    private String contents;
    @Schema(title = "본문 HTML 여부", example = "")
    private boolean html = false;
    @Hidden
    private String from;

}
