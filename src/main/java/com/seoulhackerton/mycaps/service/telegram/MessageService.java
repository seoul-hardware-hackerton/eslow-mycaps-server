package com.seoulhackerton.mycaps.service.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

@Service("messageService")
public class MessageService {

    @Autowired
    CoreTelegramService coreTelegramService;

    public String sendMsg(String text) {

        String url = "https://api.telegram.org/botToken/sendMessage?chat_id=727848241&text=";
        String msg = url + URLEncoder.encode(text);
        coreTelegramService.sendMsg(msg);
        return text;
    }
}
