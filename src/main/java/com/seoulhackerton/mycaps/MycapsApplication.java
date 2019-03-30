package com.seoulhackerton.mycaps;

import com.seoulhackerton.mycaps.components.mqtt.audioWavCallback;
import com.seoulhackerton.mycaps.components.mqtt.voiceLevelCallback;
import com.seoulhackerton.mycaps.service.MqttPublishClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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
        System.out.println("== START AUDIO WAV SUBSCRIBER ==");
        String url = Constant.ServerURI;
        String topic = Constant.AUDIO_MQTT_TOPIC;
        MqttCallback callback = new audioWavCallback();
        MqttClient client = new MqttClient(url, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        client.connect(options);
        client.setCallback(callback);
        client.subscribe(topic);
    }

    private static void voiceLevelSubscribe() throws MqttException {
        System.out.println("== START VOICE LEVEL SUBSCRIBER ==");
        String url = Constant.ServerURI;
        String topic = Constant.VOICE_MQTT_TOPIC;
        MqttCallback callback = new voiceLevelCallback();
        setMqttConfig(url, topic, callback);
    }

    private static void setMqttConfig(String url, String topic, MqttCallback callback) throws MqttException {
        MqttClient client = new MqttClient(url, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        client.connect(options);
        client.setCallback(callback);
        client.subscribe(topic);
    }
}

//subscriber.subscribe(EngineTemperatureSensor.TOPIC, (topic, msg) -> {
//        byte[] payload = msg.getPayload();
//        log.info("[I46] Message received: topic={}, payload={}", topic, new String(payload));
//        receivedSignal.countDown();
//        });