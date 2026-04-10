package com.hnix.sd.common.user;

import com.hnix.sd.common.user.service.UserEmailService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Email Controller", description = "User Email Management")
@RequiredArgsConstructor
@RequestMapping("/common/userEmail")
@RestController
public class UserEmailController {
  private final ComResponseUtil comResponseUtil;
  private final UserEmailService userEmailService;

  @Operation(summary = "사용자 추가")
  @PostMapping("/regist")
  public ComResponseDto<?> registerUserInfo(@RequestParam Integer fromIdx, Integer toIdx) {
      return comResponseUtil.setResponse200ok(userEmailService.registerUserInfo(fromIdx, toIdx));
  }
}
