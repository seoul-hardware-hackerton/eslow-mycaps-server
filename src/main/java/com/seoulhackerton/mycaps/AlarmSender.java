package com.seoulhackerton.mycaps;

import com.seoulhackerton.mycaps.service.SpeechRecognitionSamples;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

public class AlarmSender {

    private static ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
    boolean isRunning = false;

    public AlarmSender() {
        new Thread(() -> {
            isRunning = true;
            while (isRunning) {
                setSpeechRecognitionSamples();
            }
        }).start();
    }

    public void stop() {
        isRunning = false;
    }

    public void setSpeechRecognitionSamples() {
        while (!concurrentLinkedQueue.isEmpty()) {
            String filePath = concurrentLinkedQueue.poll();
            try {
                SpeechRecognitionSamples.recognitionWithAudioStreamAsync(filePath);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void add(String fileName) {
        concurrentLinkedQueue.add(fileName);
    }
}
