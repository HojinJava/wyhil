package com.hnix.sd.common.file.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter
public class FileMultiStoreDto {

    private Set<String> ids;
    private List<MultipartFile> files;

}
