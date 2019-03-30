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
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

@Service
public class SpeechRecognitionSamples {

    // The Source to stop recognition.
    private static Semaphore stopRecognitionSemaphore = new Semaphore(0);
    ;

    @Autowired
    static AzureVoice voiceConfig;

    @Autowired
    static CoreTelegramService messageService;

    private void sendTelegram(String text) {
        System.out.println("sendTelegram");
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        String sb = url + URLEncoder.encode(text);
        messageService.sendMsg(sb);
    }

    // Speech recognition with audio stream
    public void recognitionWithAudioStreamAsync(String filePath) throws InterruptedException, ExecutionException, FileNotFoundException {

//        MqttPublishClient2 client = new MqttPublishClient2();
        SpeechConfig config = SpeechConfig.fromSubscription(voiceConfig.getSubscriptionKey(), "eastasia");
        System.out.println(filePath);
        PullAudioInputStreamCallback callback = new WavStream(new FileInputStream(filePath));

        AudioConfig audioInput = AudioConfig.fromStreamInput(callback);
        // Creates a speech recognizer using audio stream input.
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioInput);
        {
            // Subscribes to events.
            recognizer.recognizing.addEventListener((s, e) -> {
                System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
            });

            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    sendTelegram("GGGGG");
                    if (e.getResult().getText().contains("help")) {

                        //텔레그램으로 살려줘 보내고 text를 가져옴
                        //TELEGRAM
//                        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.MBED_BEEF_SOUND));
                        System.out.println(e.getResult().getText());
                    } else {
//                        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.NONE));
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