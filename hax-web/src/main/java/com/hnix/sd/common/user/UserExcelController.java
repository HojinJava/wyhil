package com.hnix.sd.common.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hnix.sd.common.user.dto.UserExcelDto;
import com.hnix.sd.common.excel.util.ExcelFileNames;
import com.hnix.sd.common.user.dto.UserGridDto;
import com.hnix.sd.common.user.service.UserExcelService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name ="User Excel Controller", description = "사용자 관리 엑셀 작업 컨트롤러")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/common/user/resource")
@RestController
public class UserExcelController {

	private final UserExcelService userExcelService;
	private final ComResponseUtil comResponseUtil;

	@Operation(summary = "사용자 관리 목록 EXCEL 다운로드")
	@PostMapping("/download")
	public ResponseEntity<InputStreamResource> downloadUserExcelFile(
		@RequestBody UserGridDto searchDto
	) throws IOException {
		SXSSFWorkbook excelWorkbook = userExcelService.exportUserExcel(searchDto);
		final String excelFileName = ExcelFileNames.createExcelFileName("사용자 관리");
		return getStreamResourceResponseEntity(excelWorkbook, excelFileName);
	}

	@NotNull
	private static ResponseEntity<InputStreamResource> getStreamResourceResponseEntity(SXSSFWorkbook excelWorkbook, String excelFileName) throws IOException {
		File tmpFile = File.createTempFile("TMP~", ".xlsx");

		try (OutputStream fos = new FileOutputStream(tmpFile);) {
			excelWorkbook.write(fos);
		}

		InputStream stream = new FileInputStream(tmpFile) {
			@Override
			public void close() throws IOException {
				super.close();
				if (tmpFile.delete()) {
					log.info("임시 파일 삭제 완료");
				}
			}
		};

		return ResponseEntity.ok()
			.contentLength(tmpFile.length())
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header("Content-Disposition", String.format("attachment; fileName=\"%s.xlsx\";", excelFileName))
			.body(new InputStreamResource(stream));
	}

	@Operation(summary = "사용자 관리 목록 EXCEL 업로드")
    @PostMapping("/upload")
    public ComResponseDto<List<UserExcelDto>> uploadUserExcelFile(
            @RequestParam("file") MultipartFile file) {

        try {
            // 엑셀 파일을 메모리에서 읽어서 DTO 리스트로 변환
            List<UserExcelDto> dtoList = userExcelService.parseUserExcel(file);

            // 결과 반환
            return comResponseUtil.setResponse200ok(dtoList);

        } catch (Exception e) {
            log.error("엑셀 업로드 중 오류 발생", e);

            return comResponseUtil.setResponse200ok(List.of());
        }
    }

	@Operation(summary = "사용자 관리 목록 EXCEL 데이터 DB 저장")
	@PostMapping("/store")
	public ComResponseDto<List<String>> storeUserExcelData(
			@RequestBody List<UserExcelDto> excelDtoList) {

		try {
			// 검증 오류가 있는 데이터는 제외하고 저장
			List<UserExcelDto> validList = excelDtoList.stream()
					.filter(dto -> dto.getErrorMsg() == null || dto.getErrorMsg().trim().isEmpty())
					.toList();

			// DB 저장 및 실패 목록 반환
			List<String> failedList = userExcelService.storeUserList(validList);

			return comResponseUtil.setResponse200ok(failedList);

		} catch (Exception e) {
			log.error("엑셀 데이터 저장 중 오류 발생", e);
			// 빈 리스트 반환 (또는 전체 실패로 처리)
			return comResponseUtil.setResponse200ok(List.of());
		}
	}
}