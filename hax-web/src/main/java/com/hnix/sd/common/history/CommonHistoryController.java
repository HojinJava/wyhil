package com.hnix.sd.common.history;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.common.history.service.CommonHistoryService;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Common History Controller", description = "이력 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/common/history")
@RestController
public class CommonHistoryController {

  private final ComResponseUtil comResponseUtil;
  private final CommonHistoryService commonHistoryService;

  @Operation(summary = "이력 관리")
  @GetMapping("/all/{targetId}/{menuCd}")
  public ComResponseDto<?> getCommonHistory(@PathVariable("targetId") String targetId, @PathVariable("menuCd") String menuCd) {
      return comResponseUtil.setResponse200ok(commonHistoryService.getCommonHistory(targetId, menuCd));
  }
}
