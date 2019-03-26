//package com.seoulhackerton.mycaps;
//
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.junit.Test;
//
//public class mqttTest {
//    @Test
//    public void test() {
//        try {
//            MqttClient client = new MqttClient("tcp://52.141.36.28:1883", "zzzzz");
//            client.connect();
//            MqttMessage message = new MqttMessage();
//            message.setPayload("send my message!!aaaaasdasd".getBytes());
//            client.publish("eslow", message);
//
//            client.disconnect();
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//}
