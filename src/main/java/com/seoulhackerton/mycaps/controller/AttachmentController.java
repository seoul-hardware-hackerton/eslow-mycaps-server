package com.seoulhackerton.mycaps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulhackerton.mycaps.Constant;
import com.seoulhackerton.mycaps.service.MqttPublishClient2;
import com.seoulhackerton.mycaps.service.telegram.CoreTelegramService;
import com.seoulhackerton.mycaps.util.Util;
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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;


@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    private static final Logger logger = Logger.getLogger(AttachmentController.class);

    @Autowired
    AzureImage imageConfig;

    @Autowired
    CoreTelegramService coreTelegramService;

    private void sendTelegram(String text) {
        System.out.println("sendTelegram");
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        String sb = url + URLEncoder.encode(text);
        coreTelegramService.sendMsg(sb);
    }

    private void sendTelegramPhoto(String text, String photoPath) {
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendPhoto";
        System.out.println("sendTelegram");
        String sb = url + URLEncoder.encode(text);
        coreTelegramService.sendPhoto(sb, text, photoPath);
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity myService(@RequestParam("file") MultipartFile sourceFile) throws IOException {

        String sourceFileName = sourceFile.getOriginalFilename();
        String sourceFilenameExtension = FilenameUtils.getExtension(sourceFileName);
        File destinationFile;
        String destinationFileName;
        MqttPublishClient2 client = new MqttPublishClient2();

        do {
            destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFilenameExtension;
//            destinationFile = new File("/home/eslow/eslow-mycaps-server/attachments/" + destinationFileName);
            String currentDirectory = System.getProperty("user.dir");
            destinationFile = new File(currentDirectory, destinationFileName);
        } while (destinationFile.exists());
        sourceFile.transferTo(destinationFile);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder builder = new URIBuilder(Constant.uriBase);

            // Request parameters. All of them are optional.
            builder.setParameter("visualFeatures", "Categories,Description,Color");
            builder.setParameter("language", "en");

            // Prepare the URI for the REST API method.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            byte[] a = Util.readBytesFromFile(destinationFile.getAbsolutePath());
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", "06c31f1b012c41db8c8dce11080c6d96");
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
                if (value.getDescription().getTags().contains("laying")) {
                    sendTelegramPhoto("이곳에 쓰러진 사람이 있습니다. 여기 주소는 서울하드웨어해커톤이 열리는 서울시 금천구 디지털로 9길 90 입니다.", destinationFile.getAbsolutePath());
                    client.send("eslow/alarm", "1");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        UploadAttachmentResponse attachmentResponse = new UploadAttachmentResponse();
        attachmentResponse.setFileName(sourceFile.getOriginalFilename());
        attachmentResponse.setFileSize(sourceFile.getSize());
        attachmentResponse.setFileContentType(sourceFile.getContentType());
        attachmentResponse.setAttachmentUrl("http://localhost:9000/attachments/" + destinationFileName);
        logger.trace(imageConfig.getSubscriptionKey());

        return new ResponseEntity<>(attachmentResponse, HttpStatus.OK);
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