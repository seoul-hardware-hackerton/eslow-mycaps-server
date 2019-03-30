package com.seoulhackerton.mycaps;

public class Constant {

    public static final String IMAGE_MQTT_TOPIC = "eslow/image";
    public static final String VOICE_MQTT_TOPIC = "eslow/voice";
    public static final String AUDIO_MQTT_TOPIC = "eslow/audio";
    public static final String ALARM_MQTT_TOPIC = "eslow/alarm";
    public static final int NONE = 0;
    public static final int MBED_BEEF_SOUND = 1;
    public static final int MBED_LED_RED = 2;
    public static final int MBED_LED_GREEN = 3;
    public static final int MBED_LED_BLUE = 4;


    public static final String ServerURI = "tcp://52.141.36.28:1883";

    public static final String uriBase =
            "https://koreacentral.api.cognitive.microsoft.com/vision/v2.0/analyze";
}
