package com.Uploadify.Uploadify.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attachment {

    private String fileName;
    private String fileType;
    private String filePath;
    private String downloadUrl;

    public Attachment(String fileName, String fileType, String filePath, String downloadUrl) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.downloadUrl = downloadUrl;
    }


}
