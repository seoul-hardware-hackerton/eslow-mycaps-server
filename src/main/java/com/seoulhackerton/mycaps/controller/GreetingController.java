package com.seoulhackerton.mycaps.controller;

import com.seoulhackerton.mycaps.service.MqttPublishClient;
import com.seoulhackerton.mycaps.domain.Greeting;
import com.seoulhackerton.mycaps.domain.HelloMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    private static final Logger logger = Logger.getLogger(GreetingController.class);

    @Autowired
    MqttPublishClient publishSample;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @RequestMapping(value = "/publishToMQTT", method = RequestMethod.GET)
    public String publish(@RequestParam("topic") String topic, @RequestParam("content") String content) {
        logger.trace("publish(): " + topic + " - " + content);
        publishSample.send(topic, content);
        return "publish(): Message sent!";
    }
}
