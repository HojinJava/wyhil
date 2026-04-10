package com.hnix.sd.work.software.manage;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hnix.sd.common.excel.util.ExcelFileNames;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.ComResponseUtil;
import com.hnix.sd.work.software.manage.dto.SearchSoftwareDto;
import com.hnix.sd.work.software.manage.dto.SoftwareExcelDto;
import com.hnix.sd.work.software.manage.service.SoftwareExcelService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name ="Software Excel Controller", description = "소프트웨어 관리 엑셀 작업 컨트롤러")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/work/software/resource")
@RestController
public class SoftwareExcelController {

	private final SoftwareExcelService softwareExcelService;
    private final ComResponseUtil comResponseUtil;

	@Operation(summary = "소프트웨어 관리 목록 EXCEL 다운로드")
	@PostMapping("/download")
	public ResponseEntity<InputStreamResource> downloadSoftwareExcelFile(@RequestBody SearchSoftwareDto searchSoftwareDto) throws IOException {
		SXSSFWorkbook excelWorkbook = softwareExcelService.exportSoftwareExcel(searchSoftwareDto);
        final String excelFileName = ExcelFileNames.createExcelFileName("소프트웨어 관리");
        return getStreamResourceResponseEntity(excelWorkbook, excelFileName);
    }

    @Operation(summary = "소프트웨어 관리 목록 EXCEL 업로드")
    @PostMapping("/upload")
    public ComResponseDto<List<SoftwareExcelDto>> uploadSoftwareExcelFile(
            @RequestParam("file") MultipartFile file) {

        try {
            List<SoftwareExcelDto> dtoList = softwareExcelService.parseSoftwareExcel(file);

            return comResponseUtil.setResponse200ok(dtoList);

        } catch (Exception e) {
            log.error("엑셀 업로드 중 오류 발생", e);

            return comResponseUtil.setResponse200ok(List.of());
        }
    }

    @Operation(summary = "소프트웨어 관리 목록 DB 저장 (엑셀 업로드 후)")
    @PostMapping("/store")
    public ComResponseDto<List<String>> storeSoftwareExcelFile(
    @RequestBody List<SoftwareExcelDto> softwareList) {

    try {
        // JSON 데이터를 그대로 DB 저장
        List<String> failedList = softwareExcelService.storeSoftwareList(softwareList);
        return comResponseUtil.setResponse200ok(failedList);
    } catch (Exception e) {
        log.error("엑셀 DB 저장 중 오류 발생", e);
        throw new BizException("엑셀 DB 저장 실패: " + e.getMessage());
    }
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
}
