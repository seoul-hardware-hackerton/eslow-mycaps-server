package com.seoulhackerton.mycaps.service.telegram;

import com.fasterxml.jackson.core.type.TypeReference;
import com.seoulhackerton.mycaps.util.Util;
import com.seoulhackerton.mycaps.util.DataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("CoreTelegramService")
public class CoreTelegramService extends BaseTelegramService {


    private static final Logger logger = LoggerFactory.getLogger(CoreTelegramService.class);

    public JsonResult sendMsg(String url) {
        DataMap dataMap = new DataMap();
        DataMap result = new DataMap();

        try {
            String response = Util.sendRequest(url);
            result = objectMapper.readValue(response, new TypeReference<DataMap>() {});

        } catch (IOException e) {
            logger.info("faultStatus push faile");
        }

        logger.info("연동 결과 : {}", result);
        return new JsonResult(1, null , dataMap);

    }
}
