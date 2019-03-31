package com.seoulhackerton.mycaps.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublishClient2 {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MqttPublishClient.class);

//    @Autowired
//    MqttProperties mqttProperties;

    public void send(String topic, String content) {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient sampleClient = null;

        try {

            sampleClient = new MqttClient("tcp://" + "52.141.36.28" + ":" + "1883", RandomStringUtils.randomAlphanumeric(5), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            //http://www.hivemq.com/blog/mqtt-essentials-part-7-persistent-session-queuing-messages
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);
            logger.info("send(): Publishing message: " + content + " to topic: " + topic);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(Integer.parseInt("2"));
            message.setRetained(Boolean.FALSE);

            sampleClient.publish(topic, message);
            sampleClient.disconnect();
        } catch (MqttException e) {
            logger.info("Error on send with MqttClient...");
        }
    }
}
