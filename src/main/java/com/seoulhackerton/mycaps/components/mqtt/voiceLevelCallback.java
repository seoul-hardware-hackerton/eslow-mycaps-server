package com.seoulhackerton.mycaps.components.mqtt;

import com.seoulhackerton.mycaps.Constant;
import com.seoulhackerton.mycaps.service.MqttPublishClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class voiceLevelCallback implements MqttCallback {

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        MqttPublishClient client = new MqttPublishClient();
        byte[] voiceLevel = mqttMessage.getPayload();
        for (byte b : voiceLevel) {
            double a = ((double) (b));
            System.out.println(a);
            if (a >= 1) {
                sendAlarm(client);
            } else {
                normalAlarm(client);
            }
        }
    }

    private void normalAlarm(MqttPublishClient client) {
        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.NONE));
    }

    private void sendAlarm(MqttPublishClient client) {
        client.send(Constant.ALARM_MQTT_TOPIC, String.valueOf(Constant.MBED_BEEF_SOUND));
        //TODO 텔레그램, MQTT 알람.
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}