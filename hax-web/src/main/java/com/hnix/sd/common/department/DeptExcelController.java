package com.hnix.sd.common.department;

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

import com.hnix.sd.common.department.dto.manage.DeptExcelDownloadRequest;
import com.hnix.sd.common.department.dto.manage.DeptExcelDto;
import com.hnix.sd.common.department.dto.manage.FailedDeptDto;
import com.hnix.sd.common.department.service.DeptExcelService;
import com.hnix.sd.common.excel.util.ExcelFileNames;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.ComResponseUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name ="Dept Excel Controller", description = "회사/부서 관리 엑셀 작업 컨트롤러")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/common/dept/resource")
@RestController
public class DeptExcelController {

	private final DeptExcelService deptExcelService;
    private final ComResponseUtil comResponseUtil;

	@Operation(summary = "회사/부서 관리 목록 EXCEL 다운로드")
    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> downloadDeptExcelFile(
    @RequestBody(required = false) DeptExcelDownloadRequest request) throws IOException {
        
        // deptCd 리스트가 있으면 필터링, 없으면 전체 다운로드
        List<String> deptCd = (request != null && request.getDeptCd() != null) 
            ? request.getDeptCd() 
            : null;
        
        SXSSFWorkbook excelWorkbook = deptExcelService.exportDeptExcel(deptCd);

        final String excelFileName = ExcelFileNames.createExcelFileName("회사부서 관리");

        return getStreamResourceResponseEntity(excelWorkbook, excelFileName);
    }

    @Operation(summary = "회사/부서 관리 목록 EXCEL 업로드")
    @PostMapping("/upload")
    public ComResponseDto<List<DeptExcelDto>> uploadDeptExcelFile(
            @RequestParam("file") MultipartFile file) {

        try {
            // 엑셀 파일을 메모리에서 읽어서 DTO 리스트로 변환
            List<DeptExcelDto> dtoList = deptExcelService.parseDeptExcel(file);

            // 결과 반환
            return comResponseUtil.setResponse200ok(dtoList);

        } catch (Exception e) {
            log.error("엑셀 업로드 중 오류 발생", e);

            return comResponseUtil.setResponse200ok(List.of());
        }
    }

    @Operation(summary = "회사/부서 관리 목록 DB 저장 (엑셀 업로드 후)")
    @PostMapping("/store")
    public ComResponseDto<List<FailedDeptDto>> storeDeptExcelFile(
    @RequestBody List<DeptExcelDto> deptList) {

    try {
        // JSON 데이터를 그대로 DB 저장
        List<FailedDeptDto> failedList = deptExcelService.storeDeptList(deptList);
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