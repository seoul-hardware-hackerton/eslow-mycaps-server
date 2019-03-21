package com.seoulhackerton.mycaps;

import com.seoulhackerton.mycaps.components.mqtt.SimpleMqttCallBack;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycapsApplication {

    public static void main(String[] args) throws MqttException {
        System.out.println("== START SUBSCRIBER ==");

        MqttClient client = new MqttClient("tcp://52.141.36.28:1883", MqttClient.generateClientId());
        client.setCallback(new SimpleMqttCallBack());
        client.connect();
        client.subscribe("eslow");

        SpringApplication.run(MycapsApplication.class, args);
    }
}