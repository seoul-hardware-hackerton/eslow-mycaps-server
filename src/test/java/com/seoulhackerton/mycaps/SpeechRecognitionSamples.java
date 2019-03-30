package com.seoulhackerton.mycaps;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;
import com.seoulhackerton.mycaps.service.telegram.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

// <toplevel>
// </toplevel>


public class SpeechRecognitionSamples {

    @Autowired
    static MessageService messageService;

    // The Source to stop recognition.
    private static Semaphore stopRecognitionSemaphore;

    // Speech recognition with audio stream
    public static void recognitionWithAudioStreamAsync() throws InterruptedException, ExecutionException, FileNotFoundException {

        messageService.sendMsg("hahaha");
        stopRecognitionSemaphore = new Semaphore(0);

        // Creates an instance of a speech config with specified
        // subscription key and service region. Replace with your own subscription key
        // and service region (e.g., "westus").
        SpeechConfig config = SpeechConfig.fromSubscription("7945c92e48ec4baf806549e85850e8fe", "eastasia");

        // Create an audio stream from a wav file.
        // Replace with your own audio file name.
        PullAudioInputStreamCallback callback = new WavStream(new FileInputStream("/home/eslow/eslow-mycaps-server/attachments/whatstheweatherlike.wav"));

        AudioConfig audioInput = AudioConfig.fromStreamInput(callback);

        // Creates a speech recognizer using audio stream input.
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioInput);
        // Subscribes to events.
        recognizer.recognizing.addEventListener((s, e) -> {
//            messageService.sendMsg("hahaha1");
            System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
        });

        recognizer.recognized.addEventListener((s, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
//                messageService.sendMsg("hahaha2");
                System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
            } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
        });

        recognizer.canceled.addEventListener((s, e) -> {
//            messageService.sendMsg("hahaha3");
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
