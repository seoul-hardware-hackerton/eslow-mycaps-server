package com.seoulhackerton.mycaps.components.mqtt;

import com.seoulhackerton.mycaps.service.SpeechRecognitionSamples;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

import static javax.sound.sampled.AudioFileFormat.Type.WAVE;

public class audioWavCallback implements MqttCallback {

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        byte[] wavBytes = mqttMessage.getPayload();
        String sourceFilenameExtension = "wav";
        File destinationFile;
        String destinationFileName;

        destinationFileName = RandomStringUtils.randomAlphanumeric(10) + "." + sourceFilenameExtension;
//            destinationFile = new File("/home/eslow/eslow-mycaps-server/attachments/" + destinationFileName);
        String currentDirectory = System.getProperty("user.dir");
        destinationFile = new File(currentDirectory, destinationFileName);
        if (destinationFile.exists()) {
            byteArrayToWavFile(wavBytes, destinationFile.getAbsolutePath());
        }
        System.out.println("Audio Message received:\n\t");
        System.out.println(destinationFile.getAbsolutePath());
        SpeechRecognitionSamples.recognitionWithAudioStreamAsync(destinationFile.getAbsolutePath());
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }

    public void byteArrayToWavFile(byte[] resultArray, String filename) throws IOException {
        InputStream b_in = new ByteArrayInputStream(resultArray);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(
                filename));
        dos.write(resultArray);
        AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);

        AudioInputStream stream = new AudioInputStream(b_in, format, resultArray.length);

        File file = new File(filename);
        AudioSystem.write(stream, WAVE, file);
        System.out.println("File saved: " + file.getName() + ", bytes: " + resultArray.length);
    }
}

