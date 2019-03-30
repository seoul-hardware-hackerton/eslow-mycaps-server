package com.seoulhackerton.mycaps.components.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class voiceLevelCallback implements MqttCallback {

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        //TODO 음성 레벨 테스트 필요.
//        int level = Integer.parseInt(new String(mqttMessage.getPayload()));
//        if (level >= 2) {
//            notifyToTelegram();
//            notifyToMbed();
//        }
        byte[] abc = mqttMessage.getPayload();
        for (int i = 0; i < mqttMessage.getPayload().length; i++) {
            System.out.println("VoiceLevel Message received:\n\t" + abc[i]);
        }
    }

    private void notifyToMbed() {
        //TODO MBed
    }

    private void notifyToTelegram() {
        //TODO Telegram
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}