package com.seoulhackerton.mycaps.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity myService(@RequestParam("file") MultipartFile sourceFile) throws IOException {

        String sourceFileName = sourceFile.getOriginalFilename();
        String sourceFilenameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();

        File destinationFile;
        String destinationFileName;

        do {
            destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFilenameExtension;
            destinationFile = new File("/home/eslow/eslow-mycaps-server/attachments/" + destinationFileName);
//            destinationFile = new File("/Users/lenkim/toy-project/mycaps/attachments/" + destinationFileName);
        } while (destinationFile.exists());
//        임시 저장
        sourceFile.transferTo(destinationFile);

        UploadAttachmentResponse response = new UploadAttachmentResponse();
        response.setFileName(sourceFile.getOriginalFilename());
        response.setFileSize(sourceFile.getSize());
        response.setFileContentType(sourceFile.getContentType());
        response.setAttachmentUrl("http://localhost:9000/attachments/" + destinationFileName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @NoArgsConstructor
    @Data
    private static class UploadAttachmentResponse {
        private String fileName;
        private long fileSize;
        private String fileContentType;
        private String attachmentUrl;
    }
}
