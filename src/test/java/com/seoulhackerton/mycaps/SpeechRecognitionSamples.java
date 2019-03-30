package com.seoulhackerton.mycaps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;
import com.seoulhackerton.mycaps.components.mqtt.MqttProperties;
import com.seoulhackerton.mycaps.service.MqttPublishClient;
import com.seoulhackerton.mycaps.service.telegram.CoreTelegramService;
import com.seoulhackerton.mycaps.service.telegram.JsonResult;
import com.seoulhackerton.mycaps.service.telegram.MessageService;
import com.seoulhackerton.mycaps.util.DataMap;
import com.seoulhackerton.mycaps.util.Util;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

// <toplevel>
// </toplevel>

public class SpeechRecognitionSamples {

    // The Source to stop recognition.
    private static Semaphore stopRecognitionSemaphore;

    private void sendTelegram(String text) {
        Core core = new Core();
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        String sb = url + URLEncoder.encode(text);
        core.sendMsg(sb);
//        coreTelegramService.sendMsg(sb);
    }

    // Speech recognition with audio stream
    public void recognitionWithAudioStreamAsync() throws InterruptedException, ExecutionException, FileNotFoundException {
        System.out.println("QQQQQQQQQ");

        stopRecognitionSemaphore = new Semaphore(0);
        // Creates an instance of a speech config with specified
        // subscription key and service region. Replace with your own subscription key
        // and service region (e.g., "westus").
        SpeechConfig config = SpeechConfig.fromSubscription("1201845b3cf0469392de0f164dcd9a31", "koreacentral");

        // Create an audio stream from a wav file.
        // Replace with your own audio file name.
        PullAudioInputStreamCallback callback = new WavStream(new FileInputStream("/home/eslow/eslow-mycaps-server/attachments/whatstheweatherlike.wav"));

        AudioConfig audioInput = AudioConfig.fromStreamInput(callback);

        // Creates a speech recognizer using audio stream input.
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioInput);
        // Subscribes to events.
        MqttPublishClient2 mqttPublishClient2 = new MqttPublishClient2();
        recognizer.recognizing.addEventListener((s, e) -> {
            System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
            mqttPublishClient2.send("eslow/alarm", "foo");
        });

        recognizer.recognized.addEventListener((s, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                if (e.getResult().getText().contains("like")) {
                    mqttPublishClient2.send("eslow/alarm", "foo");
                    sendTelegram("foo");
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

class MqttPublishClient2 {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(com.seoulhackerton.mycaps.service.MqttPublishClient.class);

//    @Autowired
//    MqttProperties mqttProperties;

    public void send(String topic, String content) {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient sampleClient = null;

        try {

            sampleClient = new MqttClient("tcp://" + "52.141.36.28" + ":" + "1883", "scout-sub-test", persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            //http://www.hivemq.com/blog/mqtt-essentials-part-7-persistent-session-queuing-messages
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);
            logger.info("send(): Publishing message: " + content + " to topic: " + topic);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(Integer.parseInt("2"));
            message.setRetained(Boolean.FALSE);

            sampleClient.publish(topic, message);
            sampleClient.disconnect();
        } catch (MqttException e) {
            logger.info("Error on send with MqttClient...");
        }
    }
}
