package com.seoulhackerton.mycaps.controller;

import com.seoulhackerton.mycaps.service.MqttPublishClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AzureResultController{

    private static final Logger logger = Logger.getLogger(AzureResultController.class);

    @Autowired
    MqttPublishClient publishVoiceLevel;

    @RequestMapping(value = "/return", method = RequestMethod.GET)
    public String publish(@RequestParam("topic") String topic, @RequestParam("content") String content) {
        logger.trace("publish(): " + topic + " - " + content);
        publishVoiceLevel.send(topic, content);
        return "publish(): Message sent!";
    }
}
