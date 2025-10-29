package com.Uploadify.Uploadify.controller;

import com.Uploadify.Uploadify.entity.Attachment;
import com.Uploadify.Uploadify.response.ResponseData;
import com.Uploadify.Uploadify.service.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class AttachmentFController {

    private final AttachmentService attachmentService;

    public AttachmentFController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Attachment attachment = attachmentService.saveAttachment(file);

        // Return file metadata and download URL directly
        return new ResponseData(
                attachment.getFileName(),
                attachment.getDownloadUrl(), // S3 file URL
                attachment.getFileType(),
                file.getSize()
        );
    }

    @GetMapping("/download/{fileName}")
    public ResponseData getFile(@PathVariable String fileName) throws Exception {
        Attachment attachment = attachmentService.getAttachment(fileName);

        return new ResponseData(
                attachment.getFileName(),
                attachment.getDownloadUrl(),
                attachment.getFileType(), 0L
        );
    }
}
