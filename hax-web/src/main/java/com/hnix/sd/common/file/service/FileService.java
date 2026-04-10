package com.hnix.sd.common.file.service;

import com.hnix.sd.common.file.dao.FileDao;
import com.hnix.sd.common.file.dto.FileDto;
import com.hnix.sd.core.constant.ComConstants;
import com.hnix.sd.core.exception.BizException;
import com.hnix.sd.core.utils.DateUtil;
import com.hnix.sd.core.utils.MemberUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    
	@Value("${hnix.utils.file.location:}")
    public String location = "";
    @Value("${hnix.utils.file.max-file-size:20480000}")
    public long maxFileSize = 20480000;
    @Value("${hnix.utils.file.file-white-list:jpg,png,jpeg,ppt,doc,pdf,hwp,xls,zip,xlsx,pptx,txt,docx}")
    public List<String> fileWhiteList = new ArrayList<>();
    @Value("${hnix.utils.file.buffer-size:1048576}")
    public int bufferSize = 1048576; // 1KB
    
    
    
    private final FileDao fileDao;
    private final MemberUtil memberUtil;
    
    
    @Transactional (readOnly = true)
    public List<FileDto> getFileList(String fileId) {
    	return fileDao.findAllByFileId(fileId);
    }
    

    private String getFileUuid() {
        return UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    }


    public List<FileDto> uploadFilesWithMultiIds(Set<String> ids, List<MultipartFile> files) {
        List<FileDto> result = new ArrayList<>();

        for (var fileId : ids) {
            result.addAll( uploadFiles(files, fileId) );
        }

        return result;
    }


    @Transactional
	public List<FileDto> uploadFiles(List<MultipartFile> files, String fileId) {
		final String fileUploadLocation = location + File.separator + DateUtil.timeStamp(ComConstants.DATE_FORMAT);
        
        List<FileDto> result = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            FileDto fileDto = new FileDto();
            FileDto fileEntity = new FileDto();
            MultipartFile file = multipartFile;
            fileDto.setFileUuid(this.getFileUuid());


            String realFileNm = file.getOriginalFilename();
            String[] fileNameSplit = realFileNm.split("\\.");


            String extension = fileNameSplit[fileNameSplit.length - 1];

            if (fileWhiteList.stream().noneMatch(ext -> ext.equalsIgnoreCase(extension))) {
                throw new BizException("noUploadFile");
            }

            uploadToOneFile(fileUploadLocation, file, fileDto.getFileUuid(), maxFileSize);

            fileDto.setFileExt(extension.toLowerCase());

            fileDto.setFileId(fileId);

            fileDto.setFileNm(realFileNm);
            fileDto.setFileSize(file.getSize());

            BeanUtils.copyProperties(fileDto, fileEntity);
            fileEntity.setRegDt(LocalDateTime.now());
            fileEntity.setRegId(memberUtil.getUserId());
            fileEntity.setFilePath(fileUploadLocation);

            //DB???
            fileDao.saveAndFlush(fileEntity);

            result.add(fileDto);
        }

        return  result;
	}
    
    private String uploadToOneFile(String saveFileLocation, MultipartFile file, String saveFileName, long fileSize) {
        java.io.File dir = null;

        if (file.getSize() > fileSize) {
            throw new BizException("fileSizeExceed");
        } else {
            String tempSaveFileLocation = saveFileLocation;

            if (!"".equals(file.getOriginalFilename())) {

                dir = new java.io.File(saveFileLocation);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                try {
                    FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(tempSaveFileLocation + File.separator + saveFileName));
                } catch (Exception var10) {
                    throw new BizException("fileUploadFail");
                }
            }

            assert dir != null;
            return dir.getAbsolutePath();
        }
    }


    public void downloadFile(HttpServletResponse response, String fileUuid) {

        //log.info("Get user id ::: {}, {}", memberUtil.getUserId(), loginUserInfo.getUserId());
        log.info("# downloadFile() start");

        FileDto fileEntity = fileDao.getReferenceById(fileUuid);
        
        String absolutePath = fileEntity.getFilePath() + File.separator + fileEntity.getFileUuid();

        log.info("# absolutePath : {}", absolutePath);
        
        File f = new File(absolutePath);
        FileInputStream fis = null;

        if (!f.exists()) {
            throw new BizException("fileNotFound");
        }
        else {
            try {
                fis = new FileInputStream(f);
                int readSize = 0;
                byte[] data = new byte[bufferSize];

                String encode = UriUtils.encode(fileEntity.getFileNm(), StandardCharsets.UTF_8);
                response.addHeader("Content-Disposition", "attachment;fileName=" + "\"" + encode + "\"" + ";");
                response.setContentLength((int) f.length());
                
                if(fileEntity.getFileContType() != null && !"".equals(fileEntity.getFileContType())) {
                    response.setContentType(fileEntity.getFileContType());
                } else {
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Transfer-Encoding","binary");
                }
                
                while((readSize = fis.read(data)) != -1) {
                    response.getOutputStream().write(data);
                    response.getOutputStream().flush();
                }
                response.getOutputStream().flush();

                response.getOutputStream().close();
               
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            	if (fis != null ) try { fis.close(); } catch(Exception e) {}
            }
        }

        log.info("# downloadFile() end");
    }



    public void deleteFiles(List<String> fileUuidList) {
    	fileUuidList.forEach(this::deleteFile);
    }
    
    public void deleteFileList(String file_id) {
    	List<FileDto> files = fileDao.findAllByFileId(file_id);
    	files.forEach(i -> this.deleteFile(i.getFileUuid()));
    }

    @Transactional
    public void deleteFile(String fileUuid) {
        FileDto fileEntity = fileDao.getReferenceById(fileUuid);
        
        String absolutePath = fileEntity.getFilePath() +File.separator + fileEntity.getFileUuid();

        try {
            Files.delete(Path.of(absolutePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileDao.deleteById(fileUuid);
        fileDao.flush();
    }


}
