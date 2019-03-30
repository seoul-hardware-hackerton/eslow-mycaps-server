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
import com.seoulhackerton.mycaps.service.telegram.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

@Service
public class SpeechRecognitionSamples {

    // The Source to stop recognition.
    private static Semaphore stopRecognitionSemaphore;

    @Autowired
    static AzureVoice voiceConfig;

    @Autowired
    static MessageService messageService;

    // Speech recognition with audio stream
    public static void recognitionWithAudioStreamAsync(InputStream is) throws InterruptedException, ExecutionException, FileNotFoundException {

        stopRecognitionSemaphore = new Semaphore(0);

        MqttPublishClient client = new MqttPublishClient();
        SpeechConfig config = SpeechConfig.fromSubscription(voiceConfig.getSubscriptionKey(), "eastasia");

        PullAudioInputStreamCallback callback = new WavStream(is);

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
                    messageService.sendMsg("hahaha");
                    if (e.getResult().getText().contains("help")) {
                        //텔레그램으로 살려줘 보내고 text를 가져옴
                        //TELEGRAM

                        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.MBED_LED_RED));
//                        String sttResult = e.getResult().getText();

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
