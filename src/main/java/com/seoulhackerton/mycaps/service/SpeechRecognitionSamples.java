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
import com.seoulhackerton.mycaps.domain.WavStream;
import com.seoulhackerton.mycaps.service.telegram.CoreTelegramService;
import com.seoulhackerton.mycaps.service.telegram.JsonResult;
import com.seoulhackerton.mycaps.util.DataMap;
import com.seoulhackerton.mycaps.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

@Service
public class SpeechRecognitionSamples {

    // The Source to stop recognition.
    private static Semaphore stopRecognitionSemaphore;

    @Autowired
    static AzureVoice voiceConfig;


    private static void sendTelegram(String text) {
        Core core = new Core();
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        System.out.println("sendTelegram");
        String sb = url + URLEncoder.encode(text);
        core.sendMsg(sb);
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
}

