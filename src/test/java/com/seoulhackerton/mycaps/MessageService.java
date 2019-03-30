package com.seoulhackerton.mycaps;

import com.seoulhackerton.mycaps.service.telegram.CoreTelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

@Service
public class MessageService {

    @Autowired
    CoreTelegramService coreTelegramService;

    public String sendMsg(String text) {

        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        String msg = url + URLEncoder.encode(text);
        coreTelegramService.sendMsg(msg);


        return text;
    }
}
