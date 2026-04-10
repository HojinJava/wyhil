package com.hnix.sd.common.user.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hnix.sd.common.user.service.UserScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserConsentScheduler {
	private final UserScheduleService userScheduleService;

  @Value("${schedule.use}")
  private boolean useSchedule;
	
	@Scheduled(cron = "${schedule.cron}")
	public void run() {
		try {
      log.info("========================================");
      log.info("개인정보 동의 스케줄러 실행됨!");
      log.info("현재 시각: {}", LocalDateTime.now());
      
      if(useSchedule) {
        // Service 실행
        userScheduleService.updateUserConsentStatus();
      } else {
        log.info("useSchedule = " + useSchedule);
      }

      log.info("========================================");
		} 
		catch(Exception e) {
			log.info("* UserConsentScheduler Batch 시스템이 예기치 않게 종료되었습니다. Message: {}", e.getMessage());
      e.printStackTrace();
    }
	}
}
