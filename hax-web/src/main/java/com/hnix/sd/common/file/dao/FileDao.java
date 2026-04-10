package com.hnix.sd.common.file.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hnix.sd.common.file.dto.FileDto;

import java.util.List;

@Mapper
public interface FileDao {

    List<FileDto> findAllByFileId(@Param("fileId") String fileId);

    FileDto findByFileUuid(@Param("fileUuid") String fileUuid);

    default FileDto getReferenceById(String fileUuid) {
        return findByFileUuid(fileUuid);
    }

    void insertFile(FileDto fileEntity);

    default void saveAndFlush(FileDto fileEntity) {
        insertFile(fileEntity);
    }

    void deleteByFileUuid(@Param("fileUuid") String fileUuid);

    default void deleteById(String fileUuid) {
        deleteByFileUuid(fileUuid);
    }

    void deleteByFileId(@Param("fileId") String fileId);

    default void flush() {
        // flush is a no-op
    }
}
