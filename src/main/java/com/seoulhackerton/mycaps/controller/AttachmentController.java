package com.seoulhackerton.mycaps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulhackerton.mycaps.Util;
import com.seoulhackerton.mycaps.domain.AzureImage;
import com.seoulhackerton.mycaps.domain.Image.ImageRes;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;


@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    private static final Logger logger = Logger.getLogger(AttachmentController.class);

    @Autowired
    AzureImage imageConfig;

    private static final String uriBase =
            "https://koreacentral.api.cognitive.microsoft.com/vision/v2.0/analyze";

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity myService(@RequestParam("file") MultipartFile sourceFile) throws IOException {

        String sourceFileName = sourceFile.getOriginalFilename();
        String sourceFilenameExtension = FilenameUtils.getExtension(sourceFileName);

        File destinationFile;
        String destinationFileName;

        do {
            destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFilenameExtension;
//            destinationFile = new File("/home/eslow/eslow-mycaps-server/attachments/" + destinationFileName);
            destinationFile = new File("/Users/lenkim/toy-project/mycaps/attachments/" + destinationFileName);
        } while (destinationFile.exists());

//        임시 저장
        sourceFile.transferTo(destinationFile);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder builder = new URIBuilder(uriBase);

            // Request parameters. All of them are optional.
            builder.setParameter("visualFeatures", "Categories,Description,Color");
            builder.setParameter("language", "en");

            // Prepare the URI for the REST API method.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
//             sourceFile.getBytes();
            byte[] a = Util.readBytesFromFile(destinationFile.getAbsolutePath());
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", imageConfig.getSubscriptionKey());
            // Request body.
            HttpEntity byteArrayEntity = new ByteArrayEntity(a);
            request.setEntity(byteArrayEntity);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                ObjectMapper mapper = new ObjectMapper(); // create once, reuse
                ImageRes value = mapper.readValue(jsonString, ImageRes.class);
                System.out.println("REST Response:\n");
                System.out.println(value.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        UploadAttachmentResponse response2 = new UploadAttachmentResponse();
        response2.setFileName(sourceFile.getOriginalFilename());
        response2.setFileSize(sourceFile.getSize());
        response2.setFileContentType(sourceFile.getContentType());
        response2.setAttachmentUrl("http://localhost:9000/attachments/" + destinationFileName);
        logger.trace(imageConfig.getSubscriptionKey());
        logger.trace(imageConfig.getUrlBase());

        return new ResponseEntity<>(response2, HttpStatus.OK);

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
