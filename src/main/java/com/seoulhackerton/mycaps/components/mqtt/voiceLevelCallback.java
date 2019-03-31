package com.seoulhackerton.mycaps.components.mqtt;

import com.seoulhackerton.mycaps.AlarmSender;
import com.seoulhackerton.mycaps.Constant;
import com.seoulhackerton.mycaps.service.MqttPublishClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class voiceLevelCallback implements MqttCallback {

    private VoiceSender alarmSender;

    public voiceLevelCallback(VoiceSender alarmSender) {
        this.alarmSender = alarmSender;
    }

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        alarmSender.add(new String(mqttMessage.getPayload()));
        System.out.println("Message received:\n\t" + new String(mqttMessage.getPayload()));
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}