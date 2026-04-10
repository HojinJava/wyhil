package com.hnix.sd.work.registration.survey;

import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.registration.certificate.dto.StoreCertificateDto;
import com.hnix.sd.work.registration.survey.dto.StoreSurveyDto;
import com.hnix.sd.work.registration.survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Survey Controller", description = "설문조사 조회 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/work/survey")
@RestController
public class SurveyController {

    private final SurveyService surveyService;
    private final ComResponseUtil responseUtil;


    @Operation(summary = "설문조사 조회")
    @GetMapping("/{serviceNo}")
    public ComResponseDto<?> getSurveyByServiceNo(@PathVariable("serviceNo") String serviceNo) {
        return responseUtil.setResponse200ok( surveyService.getSurveyByServiceNo(serviceNo) );
    }

    @Operation(summary = "설문조사 완료여부 확인")
    @GetMapping("/check/exist/{serviceNo}")
    public ComResponseDto<?> checkSurveyByServiceNo(@PathVariable("serviceNo") String serviceNo) {
        return responseUtil.setResponse200ok( surveyService.checkSurveyByServiceNo(serviceNo) );
    }

    /** 고객사 설문조사 화면에서 사용하는 API */
    @Operation(summary = "설문조사를 위한 작업 등록 정보 조회")
    @GetMapping("/client/finder")
    public ComResponseDto<?> getRegistrationWorkInfo(@RequestParam(value = "link") String serviceLink) throws NotFoundException {
        return responseUtil.setResponse200ok( surveyService.getWorkRegistrationFromServiceNo(serviceLink) );
    }

    @Operation(summary = "개인정보 활용 동의 여부 확인")
    @GetMapping("/client/check/consent")
    public ComResponseDto<?> checkConsentUsedLink(@RequestParam(value = "link") String serviceLink) {
        return responseUtil.setResponse200ok( surveyService.checkConsentUsedLink(serviceLink) );
    }

    @Operation(summary = "개인정보 활용 동의")
    @GetMapping("/client/agree")
    public ComResponseDto<?> consentToPersonalInformation(@RequestParam(value = "link") String serviceLink) {
        surveyService.consentToPersonalInformation(serviceLink);
        return responseUtil.setResponse200ok();
    }


    @Operation(summary = "설문조사 정보 저장")
    @PostMapping("/client/store/survey")
    public ComResponseDto<?> storeCustomerSurvey(@RequestBody StoreSurveyDto dto) throws NotFoundException {
        surveyService.storeCustomerSurvey(dto);
        return responseUtil.setResponse200ok();
    }

    @Operation(summary = "승인/반려 여부 정보 저장")
    @PostMapping("/client/store/certificate")
    public ComResponseDto<?> storeCustomerCertificate(@RequestBody StoreCertificateDto dto) throws NotFoundException {
        surveyService.storeCustomerCertificate(dto);
        return responseUtil.setResponse200ok();
    }

}
