package com.seoulhackerton.mycaps.controller;

import com.seoulhackerton.mycaps.domain.AzureImage;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping("/attachments")
public class AttachmentController {

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
        UploadAttachmentResponse response2 = new UploadAttachmentResponse();
        response2.setFileName(sourceFile.getOriginalFilename());
        response2.setFileSize(sourceFile.getSize());
        response2.setFileContentType(sourceFile.getContentType());
        response2.setAttachmentUrl("http://localhost:9000/attachments/" + destinationFileName);

//        //TODO 이미지를 원격으로 넘겨주기.
////        ImageRes responseJson =  sendImageToRemote(sourceFile);
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//
//        try {
//            URIBuilder builder = new URIBuilder(uriBase);
//
//            // Request parameters. All of them are optional.
//            builder.setParameter("visualFeatures", "Categories,Description,Color");
//            builder.setParameter("language", "en");
//
//            // Prepare the URI for the REST API method.
//            URI uri = builder.build();
//            HttpPost request = new HttpPost(uri);
//
//            // Request headers.
//            byte[] a = sourceFile.getBytes();
//
//            request.setHeader("Content-Type", "application/octet-stream");
//            request.setHeader("Ocp-Apim-Subscription-Key", imageConfig.getSubscriptionKey());
//            // Request body.
//            HttpEntity byteArrayEntity = new ByteArrayEntity(a);
//            request.setEntity(byteArrayEntity);
//
//            HttpResponse response = httpClient.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//                // Format and display the JSON response.
//                String jsonString = EntityUtils.toString(entity);
////                System.out.println(jsonString);
//                ObjectMapper mapper = new ObjectMapper(); // create once, reuse
//                ImageRes value = mapper.readValue(jsonString, ImageRes.class);
//                System.out.println("REST Response:\n");
//                System.out.println(value.toString());
//            }
//        } catch (Exception e) {
//            // Display error message.
//            System.out.println(e.getMessage());
//        }

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
