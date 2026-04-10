package com.hnix.sd.work.registration.record.service;

import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserRegistDto;
import com.hnix.sd.common.user.service.UserService;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.work.registration.certificate.service.CertificateService;
import com.hnix.sd.work.registration.record.dao.WorkRegistrationDao;
import com.hnix.sd.work.registration.record.dto.*;
import com.hnix.sd.work.registration.survey.service.SurveyService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class WorkRegistrationService {

	private final UserService userService;
	private final CertificateService certificateService;
	private final SurveyService surveyService;

	private final WorkRegistrationDao registrationDao;
	private final UserDao userDao;

	private Set<String> duplicatedServiceNo;

	//작업 등록
	@Transactional
	public RegistrationResultDto registerMultiWorkContents(WorkRegisterDto registerDto) {
		System.out.println("# [WorkRegistrationService.java] registerMultiWorkContents() start");

		//var requesters = registerDto.getRequester();
		List<UserRegistDto> requesters = registerDto.getRequester();

		List<FindWorkRegistrationDto> entities = new ArrayList<>();

		//이메일 없는 사용자 목록?
		List<UserRegistDto> failedStoreUser = userService.multiRegisterUserInfo( requesters );

		List<RequesterUserIdDto> requesterIds = new ArrayList<>();

		final String prefix = registerDto.getSubCd();
		LocalDateTime reqDate = registerDto.getReqDate().atStartOfDay();
		LocalDateTime procDate = registerDto.getProcDt().atStartOfDay();

		duplicatedServiceNo = searchDuplicateServiceNo( prefix );

		int sequence = duplicatedServiceNo.size();

		for (var user : requesters) {

			String newServiceNo = String.format("%s_%04d", prefix, sequence++);

			while ( duplicatedServiceNo.contains(newServiceNo) ) {
				if (sequence > 9999) {
					throw new BizException("생성 가능한 Service No 개수를 초과하였습니다.");
				}
				newServiceNo = String.format("%s_%04d", prefix, sequence++);
			}

			FindWorkRegistrationDto entity = new FindWorkRegistrationDto();
			entity.setServiceNo(newServiceNo);
			entity.setServiceCd(registerDto.getServiceCd());
			entity.setReqDt(reqDate);
			entity.setProcDt(procDate);
			entity.setReqSupportCd(registerDto.getReqSupportCd());
			entity.setReqContents(registerDto.getReqContents());
			entity.setProcSupportCd(registerDto.getProcSupportCd());
			entity.setProcContents(registerDto.getProcContents());
			entity.setStatusCd(registerDto.getStatusCode());
			entity.setRegDt(LocalDateTime.now());
			entity.setRegId(registerDto.getRegId());
			
			// 이메일로 실제 userId 조회
			UserDto actualUser = userDao.findByUserEmail(user.getUserEmail())
			.orElseThrow(() -> new BizException("사용자 정보를 찾을 수 없습니다"));
			String requestUserId = actualUser.getUserId();

			entity.setReqUserId(requestUserId);


			requesterIds.add( new RequesterUserIdDto(newServiceNo, requestUserId) );
			entities.add(entity);
		}

		registrationDao.saveAll(entities);

		// 설문조사 데이터 등록
		surveyService.createSurveyFormWork(requesterIds);

		// 승인현황 데이터 등록
		certificateService.createCertificateFormWork(requesterIds);

		for (FindWorkRegistrationDto entity : entities) {
			registrationDao.insertSubRegister(entity.getServiceNo());
		}

		return new RegistrationResultDto(failedStoreUser, requesterIds);
	}

	private Set<String> searchDuplicateServiceNo(final String serviceNoPrefix) {
		return registrationDao.findByServiceNoStartingWith(serviceNoPrefix);
	}

	public FindWorkRegistrationDto getWorkRegistrationFromServiceNo(final String serviceNo) throws NotFoundException {
		FindWorkRegistrationDto result = registrationDao.findWorkRegistrationByServiceNo(serviceNo);
		if (result == null) {
			throw new NotFoundException(String.format("'%s' 서비스 번호로 등록된 작업이 없습니다.", serviceNo));
		}
		return result;
	}

	public void updateServiceInfoByServiceNo(UpdateWorkRegistrationDto updateDto) throws NotFoundException {

		FindWorkRegistrationDto entity = registrationDao.findById(updateDto.getServiceNo());
		if (entity == null) {
			throw new NotFoundException("서비스 번호가 존재하지 않습니다.");
		}

		if ( !StringUtils.isEmpty(entity.getServiceNo()) ) {

			entity.setServiceCd(updateDto.getServiceCd());				//서비스항목

			//요청일자
			LocalDateTime reqDate = updateDto.getReqDate().atStartOfDay();
			entity.setReqDt( reqDate );

			entity.setReqSupportCd(updateDto.getReqSupportCd());		//요청매체
			entity.setReqContents(updateDto.getReqContents());			//요청내용

			//처리일자
			LocalDateTime procDate = updateDto.getProcDt().atStartOfDay();
			entity.setProcDt( procDate );

			entity.setProcSupportCd(updateDto.getProcSupportCd());		//처리매체
			entity.setStatusCd(updateDto.getStatusCd());				//처리상태
			entity.setProcContents(updateDto.getProcContents());		//처리내용
			entity.setRemark(updateDto.getRemark());					//비고

			entity.setModDt(LocalDateTime.now());						//수정일
			entity.setModId(updateDto.getModId());						//수정자

			registrationDao.updateServiceInfoByServiceNo(updateDto);
		}
	}
}
