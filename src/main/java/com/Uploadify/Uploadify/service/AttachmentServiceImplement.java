package com.Uploadify.Uploadify.service;

import com.Uploadify.Uploadify.entity.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;


@Service
public class AttachmentServiceImplement implements AttachmentService{

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketname;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence: " + fileName);
            }

            // Create a unique key for S3
            String key = "uploads/" + Instant.now().toEpochMilli() + "_" + fileName;

            // Upload file to S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketname)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            // Generate download URL
            String downloadUrl = "https://" + bucketname + ".s3." + region + ".amazonaws.com/" + key;

            // Save attachment metadata
            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setFileType(file.getContentType());
            attachment.setDownloadUrl(downloadUrl);

            // Return attachment
            return attachment;

        } catch (IOException e) {
            throw new Exception("Failed to read file bytes for upload: " + fileName, e);
        } catch (Exception e) {
            throw new Exception("Could not upload the file to S3: " + fileName, e);
        }
    }




    @Override
    public Attachment getAttachment(String key) throws Exception {
        try {
            // Generate a presigned URL
            URL fileUrl = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketname).key(key));

            Attachment attachment = new Attachment();
            attachment.setFilePath(key);
            attachment.setDownloadUrl(fileUrl.toString());
            return attachment;
        } catch (Exception e) {
            throw new Exception("File not found in S3 with key: " + key, e);
        }
    }

}
