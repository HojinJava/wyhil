package com.hnix.sd.common.excel;

import com.hnix.sd.common.excel.dto.ReportSubcodeExcelParams;
import com.hnix.sd.common.excel.dto.WorkRegistrationExportParams;
import com.hnix.sd.common.excel.dto.WorkResultExportParams;
import com.hnix.sd.common.excel.service.ReportSubcodeExcelExportService;
import com.hnix.sd.common.excel.service.WorkRegistrationExcelExportService;
import com.hnix.sd.common.excel.service.WorkResultExcelExportService;
import com.hnix.sd.common.excel.util.ExcelFileNames;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@Tag(name ="Work Excel Controller", description = "작업 등록/처리 현황 엑셀 작업 컨트롤러")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/work/export")
@RestController
public class WorkExcelController {

    private final WorkRegistrationExcelExportService workRegistrationExcelExportService;
    private final WorkResultExcelExportService workResultExcelExportService;
    private final ReportSubcodeExcelExportService reportSubcodeExcelExportService;

    @Operation(summary = "작업 등록 현황 목록 EXCEL 파일 다운로드")
    @PostMapping("/registration")
    public ResponseEntity<InputStreamResource> downloadWorkRegistrationExcelFile(@RequestBody WorkRegistrationExportParams params) throws IOException {
        SXSSFWorkbook excelWorkbook = workRegistrationExcelExportService.workRegistrationExcelExport(params);

        final String excelFileName = ExcelFileNames.createExcelFileName("작업등록현황");

        return getStreamResourceResponseEntity(excelWorkbook, excelFileName);
    }

    @Operation(summary = "작업 처리 현황 목록 EXCEL 파일 다운로드")
    @PostMapping("/result")
    public ResponseEntity<InputStreamResource> downloadWorkResultExcelFile(@RequestBody WorkResultExportParams params) throws IOException {
        SXSSFWorkbook excelWorkbook = workResultExcelExportService.workResultExcelExport(params);

        final String excelFileName = ExcelFileNames.createExcelFileName("작업처리현황");
        //res.setContentType("ms-vnd/excel; UTF-8");

        return getStreamResourceResponseEntity(excelWorkbook, excelFileName);
    }

    @Operation(summary = "보고서(SUB CODE 기준) 목록 EXCEL 파일 다운로드")
    @PostMapping("/reportSubcode")
    public ResponseEntity<InputStreamResource> downloadReportSubcodeExcelFile(@RequestBody ReportSubcodeExcelParams params) throws IOException {
        XSSFWorkbook excelWorkbook = reportSubcodeExcelExportService.reportSubcodeExcelExport(params);

        final String excelFileName = ExcelFileNames.createExcelFileName("보고서(SUB CODE 기준)");

        return getStreamResourceResponseEntity2(excelWorkbook, excelFileName);
    }

    @Operation(summary = "작업등록현황(일괄)) 양식 EXCEL 파일 다운로드")
    @PostMapping("/regMulti")
    public ResponseEntity<InputStreamResource> downloadReportRegMultiExcelFile() throws IOException {
        XSSFWorkbook excelWorkbook = workRegistrationExcelExportService.regMultiExcelExport();

        final String excelFileName = ExcelFileNames.createExcelFileName("작업등록현황(일괄)");

        return getStreamResourceResponseEntity2(excelWorkbook, excelFileName);
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

    @NotNull
    private static ResponseEntity<InputStreamResource> getStreamResourceResponseEntity2(XSSFWorkbook excelWorkbook, String excelFileName) throws IOException {
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