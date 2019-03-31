package com.seoulhackerton.mycaps.components.mqtt;

import com.seoulhackerton.mycaps.Constant;
import com.seoulhackerton.mycaps.service.MqttPublishClient;
import com.seoulhackerton.mycaps.service.MqttPublishClient2;
import com.seoulhackerton.mycaps.service.SpeechRecognitionSamples;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

public class VoiceSender {

    private static ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
    boolean isRunning = false;

    public VoiceSender() {
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

    private void normalAlarm(MqttPublishClient2 client) {
        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.NONE));
    }

    private void sendAlarm(MqttPublishClient2 client) {
        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.MBED_BEEF_SOUND));
    }


    public void setSpeechRecognitionSamples(MqttPublishClient2 client) {
        while (!concurrentLinkedQueue.isEmpty()) {
            String filePath = concurrentLinkedQueue.poll();
            Double a = Double.parseDouble(filePath);
            if (a >= 1) {
                sendAlarm(client);
            } else {
                normalAlarm(client);
            }
        }
    }

    public void add(String fileName) {
        concurrentLinkedQueue.add(fileName);
    }
}