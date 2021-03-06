package com.seoulhackerton.mycaps.service.telegram;

import com.fasterxml.jackson.core.type.TypeReference;
import com.seoulhackerton.mycaps.util.Util;
import com.seoulhackerton.mycaps.util.DataMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

import static com.seoulhackerton.mycaps.util.Util.readBytesFromFile;

@Service("CoreTelegramService")
public class CoreTelegramService extends BaseTelegramService {


    private static final Logger logger = LoggerFactory.getLogger(CoreTelegramService.class);

    public JsonResult sendMsg(String url) {
        DataMap dataMap = new DataMap();
        DataMap result = new DataMap();

        try {
            String response = Util.sendRequest(url);
            result = objectMapper.readValue(response, new TypeReference<DataMap>() {
            });

        } catch (IOException e) {
            logger.info("faultStatus push faile");
        }

        logger.info("연동 결과 : {}", result);
        return new JsonResult(1, null, dataMap);

    }
    // TODO 미완성으로 프로젝트 종료
    public void sendPhoto(String url, String message, String filePath) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            URIBuilder builder = new URIBuilder(url);

            // Request parameters. All of them are optional.
            builder.setParameter("chat_id", "727848241");
            builder.setParameter("text", message);

            // Prepare the URI for the REST API method.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            // Request headers.
            byte[] a = readBytesFromFile(filePath);

            request.setHeader("Content-Type", "multipart/form-data");
            // Request body.
            HttpEntity byteArrayEntity = new ByteArrayEntity(a);
            request.setEntity(byteArrayEntity);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                System.out.println(jsonString);
//                ObjectMapper mapper = new ObjectMapper(); // create once, reuse
                // TODO RESPONSE
//                PhotoResponse value = mapper.readValue(jsonString, PhotoResponse.class);
//                System.out.println("REST Response:\n");
//                System.out.println(value.toString());
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
    }
}
