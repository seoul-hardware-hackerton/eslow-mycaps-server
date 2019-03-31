package com.seoulhackerton.mycaps;

import com.seoulhackerton.mycaps.service.MqttPublishClient2;
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
            MqttPublishClient2 client = new MqttPublishClient2();
            while (isRunning) {
                setSpeechRecognitionSamples(client);
            }
        }).start();
    }

    public void stop() {
        isRunning = false;
    }

    public void setSpeechRecognitionSamples(MqttPublishClient2 client) {
        while (!concurrentLinkedQueue.isEmpty()) {
            String filePath = concurrentLinkedQueue.poll();
            try {
                SpeechRecognitionSamples.recognitionWithAudioStreamAsync(filePath, client);
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
