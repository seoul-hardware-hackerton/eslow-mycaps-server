package com.seoulhackerton.mycaps;

import com.seoulhackerton.mycaps.components.mqtt.audioWavCallback;
import com.seoulhackerton.mycaps.components.mqtt.voiceLevelCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycapsApplication {

    public static void main(String[] args) throws MqttException {

        voiceLevelSubscribe();
        audioWavSubscribe();
        SpringApplication.run(MycapsApplication.class, args);
    }

    private static void audioWavSubscribe() throws MqttException {
        System.out.println("== START Audio Wav SUBSCRIBER ==");
        MqttClient client = new MqttClient("tcp://52.141.36.28:1883", MqttClient.generateClientId());
        client.setCallback(new audioWavCallback());
        client.connect();
        client.subscribe("eslow/audio");
    }

    private static void voiceLevelSubscribe() throws MqttException {
        System.out.println("== START Voice Level SUBSCRIBER ==");
        MqttClient client = new MqttClient("tcp://52.141.36.28:1883", MqttClient.generateClientId());
        client.setCallback(new voiceLevelCallback());
        client.connect();
        client.subscribe("eslow/voice");
    }
}