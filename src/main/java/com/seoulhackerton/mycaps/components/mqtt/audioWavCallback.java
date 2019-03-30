package com.seoulhackerton.mycaps.components.mqtt;

import com.seoulhackerton.mycaps.service.SpeechRecognitionSamples;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class audioWavCallback implements MqttCallback {

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        //Bytes
        byte[] wavBytes = mqttMessage.getPayload();
        for (byte wavByte : wavBytes) {
            System.out.println(wavByte);
        }

        SpeechRecognitionSamples.recognitionWithAudioStreamAsync(new ByteArrayInputStream(wavBytes));
        System.out.println("Audio Message received:\n\t"+ new String(mqttMessage.getPayload()) );
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}