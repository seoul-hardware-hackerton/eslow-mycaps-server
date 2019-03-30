package com.seoulhackerton.mycaps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VoiceTest {

    @Test
    public void voiceTest() throws InterruptedException, ExecutionException, FileNotFoundException {

        SpeechRecognitionSamples.recognitionWithAudioStreamAsync();
    }
}