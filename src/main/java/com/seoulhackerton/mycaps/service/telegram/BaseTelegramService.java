package com.seoulhackerton.mycaps.service.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseTelegramService {

    @Autowired
    protected ObjectMapper objectMapper;
}
