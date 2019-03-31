package com.seoulhackerton.mycaps.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;
import com.seoulhackerton.mycaps.Constant;
import com.seoulhackerton.mycaps.domain.AzureVoice;
import com.seoulhackerton.mycaps.domain.PhotoResponse;
import com.seoulhackerton.mycaps.domain.WavStream;
import com.seoulhackerton.mycaps.service.telegram.CoreTelegramService;
import com.seoulhackerton.mycaps.service.telegram.JsonResult;
import com.seoulhackerton.mycaps.util.DataMap;
import com.seoulhackerton.mycaps.util.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

@Service
public class SpeechRecognitionSamples {

    // The Source to stop recognition.
    private static Semaphore stopRecognitionSemaphore;

    @Autowired
    static AzureVoice voiceConfig;


    private static void sendTelegram(String text) {
        Core core = new Core();
//        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        System.out.println("sendTelegram");
        String sb = url + URLEncoder.encode(text);
        core.sendMsg(sb);
//        core.sendPhoto(sb, text, photoPath);
    }

    private static void sendTelegramPhoto(String text, String photoPath) {
        Core core = new Core();
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendPhoto";
        System.out.println("sendTelegram");
        String sb = url + URLEncoder.encode(text);
        core.sendPhoto(sb, text, photoPath);
    }

    // Speech recognition with audio stream
    public static void recognitionWithAudioStreamAsync(String filePath, MqttPublishClient2 client) throws InterruptedException, ExecutionException, FileNotFoundException {
        stopRecognitionSemaphore = new Semaphore(0);
        SpeechConfig config = SpeechConfig.fromSubscription("1201845b3cf0469392de0f164dcd9a31", "koreacentral");
        System.out.println(filePath);
        PullAudioInputStreamCallback callback = new WavStream(new FileInputStream(filePath));

        AudioConfig audioInput = AudioConfig.fromStreamInput(callback);
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioInput);

        {
            recognizer.recognizing.addEventListener((s, e) -> {
                System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
                if (e.getResult().getText().contains("help")) {
                    System.out.println("Hi");
                    client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.MBED_BEEF_SOUND));
                } else {
                    client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.NONE));
                }
            });

            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    if (e.getResult().getText().contains("help")) {
                        sendTelegram("도움을 요청하고 있습니다!");
                        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.MBED_BEEF_SOUND));
                        System.out.println(e.getResult().getText());
                    } else {
                        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.NONE));
                    }
                    System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
                } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                    System.out.println("NOMATCH: Speech could not be recognized.");
                }
            });

            recognizer.canceled.addEventListener((s, e) -> {
                System.out.println("CANCELED: Reason=" + e.getReason());

                if (e.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + e.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + e.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }
                stopRecognitionSemaphore.release();
            });

            recognizer.sessionStarted.addEventListener((s, e) -> {
                System.out.println("\nSession started event.");
            });

            recognizer.sessionStopped.addEventListener((s, e) -> {
                System.out.println("\nSession stopped event.");

                // Stops translation when session stop is detected.
                System.out.println("\nStop translation.");
                stopRecognitionSemaphore.release();
            });

            // Starts continuous recognition. Uses stopContinuousRecognitionAsync() to stop recognition.
            recognizer.startContinuousRecognitionAsync().get();

            // Waits for completion.
            stopRecognitionSemaphore.acquire();

            // Stops recognition.
            recognizer.stopContinuousRecognitionAsync().get();
        }
    }
}

class Core {

    protected ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(CoreTelegramService.class);

    public JsonResult sendMsg(String url) {
        DataMap dataMap = new DataMap();
        DataMap result = new DataMap();

        try {
            String response = Util.sendRequest(url);
            result = objectMapper.readValue(response, new TypeReference<DataMap>() {
            });

        } catch (IOException e) {
            logger.info("faultStatus push faile");
        }

        logger.info("연동 결과 : {}", result);
        return new JsonResult(1, null, dataMap);

    }

    public void sendPhoto(String url, String message, String filePath) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            URIBuilder builder = new URIBuilder(url);

            // Request parameters. All of them are optional.
            builder.setParameter("chat_id", "727848241");
            builder.setParameter("text", message);

            // Prepare the URI for the REST API method.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            // Request headers.
            byte[] a = readBytesFromFile(filePath);

            request.setHeader("Content-Type", "multipart/form-data");
            // Request body.
            HttpEntity byteArrayEntity = new ByteArrayEntity(a);
            request.setEntity(byteArrayEntity);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                System.out.println(jsonString);
                ObjectMapper mapper = new ObjectMapper(); // create once, reuse
                //TODO RESPONSE
                PhotoResponse value = mapper.readValue(jsonString, PhotoResponse.class);
                System.out.println("REST Response:\n");
                System.out.println(value.toString());
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
    }
//     Replace <Subscription Key> with your valid subscription key.

    private static byte[] readBytesFromFile(String filePath) throws FileNotFoundException {

        FileInputStream fileInputStream = null;

        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            System.out.println(file.getName());
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bytesArray;

    }
}


//
