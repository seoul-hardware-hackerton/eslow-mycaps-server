package com.seoulhackerton.mycaps.service;

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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

@Service
public class SpeechRecognitionSamples {

    // The Source to stop recognition.
    private static Semaphore stopRecognitionSemaphore;

    @Autowired
    static AzureVoice voiceConfig;

    @Autowired
    static CoreTelegramService messageService;

    private static void sendTelegram(String text) {
        System.out.println("sendTelegram");
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        String sb = url + URLEncoder.encode(text);
        messageService.sendMsg(sb);
    }

    // Speech recognition with audio stream
    public static void recognitionWithAudioStreamAsync(String filePath, MqttPublishClient2 client) throws InterruptedException, ExecutionException, FileNotFoundException {
        stopRecognitionSemaphore = new Semaphore(0);
        SpeechConfig config = SpeechConfig.fromSubscription("06a7558e68a142f8838f80035deb6ad3", "koreacentral");
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
                        System.out.println("Hi");
                        sendTelegram("Help 살려주세요 텔그래");
                        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.MBED_BEEF_SOUND));
                        System.out.println(e.getResult().getText());
                    } else {
                        System.out.println("Hi22222");
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

