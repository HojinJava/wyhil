package com.hnix.sd.common.file;

import com.hnix.sd.common.file.dto.FileDto;
import com.hnix.sd.common.file.service.FileService;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Tag(name = "File Controller", description = "공통 파일 관리 컨트롤러")
@RequiredArgsConstructor
@RequestMapping(value = "/common/file")
@RestController
public class FileController {
	
	private final FileService  fileService;
	private final ComResponseUtil comResponseUtil;


    @Operation(summary = "단일 파일 업로드")
    @PostMapping("/{file_id}")
    public ComResponseDto<?> postFile(HttpServletRequest request,
                                      @Parameter(description = "FILE_ID - 파일 등록 구분자 (KEY)" ) @PathVariable String file_id,
                                      @RequestParam(value = "files") List<MultipartFile> files) {
        return comResponseUtil.setResponse200ok(fileService.uploadFiles(files, file_id));
    }

    @Operation(summary = "다중 ID 파일 업로드 (동일 파일 다중 등록)")
    @PostMapping("/upload/multi-id")
    public ComResponseDto<?> storeFileByMultiIds(HttpServletRequest request,
                                                 @RequestParam(value = "ids") Set<String> ids,
                                                 @RequestParam(value = "files") List<MultipartFile> files) {
        return comResponseUtil.setResponse200ok(fileService.uploadFilesWithMultiIds(ids, files));
    }
    

	@Operation(summary = "등록된 파일 목록 조회")
    @GetMapping("/list/{file_id}")
    public ComResponseDto<List<FileDto>> getFileList( @Parameter (description="FILE_ID - 파일 등록 구분자 (KEY)" ) @PathVariable String file_id){
        return (ComResponseDto<List<FileDto>>) comResponseUtil.setResponse200ok(fileService.getFileList(file_id));
    }
    
    @Operation(summary = "파일 다운로드")
    @GetMapping("/download/{file_uuid}")
    public void fileDownLoad(HttpServletResponse res,  @Parameter (description="FILE_UUID - 파일 UUID" ) @PathVariable String file_uuid ){
        fileService.downloadFile(res, file_uuid);
    }
    
    @Operation(summary = "파일 목록 전체 삭제")
    @DeleteMapping("/list/{file_id}")
    public ComResponseDto<?> deleteFiles (@Parameter (description="FILE_ID - 파일 등록 구분자 (KEY)" ) @PathVariable String file_id) {
    	fileService.deleteFileList(file_id);
    	return  comResponseUtil.setResponse200ok();
    }
    
    
    @Operation(summary = "단일 파일 삭제")
    @DeleteMapping("/{file_uuid}")
    public ComResponseDto<?> deleteFile (@Parameter (description="FILE_UUID - 파일 UUID" ) @PathVariable String file_uuid) {
    	fileService.deleteFile(file_uuid);
    	return  comResponseUtil.setResponse200ok();
   }
   
    @Operation(summary = "다중 파일 삭제 (UUID 목록)")
    @DeleteMapping("")
    public ComResponseDto<?> deleteFileList (@Parameter (description="FILE_UUID - 파일 UUID 목록" ) @RequestBody List<String> file_uuid_list) {
    	fileService.deleteFiles(file_uuid_list);
    	return  comResponseUtil.setResponse200ok();
   }

}
