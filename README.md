# MyCaps

배경.

길거리나 집안에서 위험한 상황을 인공지능이 인식할 수는 없을까?

시스템 구상도
![](https://ws2.sinaimg.cn/large/006tNc79gy1g1t3qwkewej30pr0hnn04.jpg)

결과물

Iot 기기  
![](https://ws1.sinaimg.cn/large/006tKfTcgy1g1pin5a5kyj303w03qt94.jpg)

동영상

누워있는 사람 인식: https://youtu.be/2qeVXpQ4lbw  
목소리 인식: https://youtu.be/hMTZGZuYJtE  

### 개발 회고.
1. 스프링부트 활용하기.

https://spring.io/guides/gs/spring-boot/  
여기서 하는 튜트리얼 보고 시작했던 스프링부트를 성공리에 해커톤을 마칠수 있어서 다행이다.

스프링부트2를 사용하면서 느낀점은, 내가 스프링부트를 전적으로 신뢰하지 못하고 있다는 생각이 들었다. 특히 @Autowire와 같은 어노테이션으로 동작되는 코드의 경우, 설정을 잘못했는지, 이해가 잘못되었는지 원활히 동작됨을 확인 했는데, 막상 다른 기능을 추가시킬 때 또 다시 검증하는 내 모습을 보면서 신뢰하지 못하고 있다는 생각이 들었다.



좋았던 점은, 스프링부트가 출연되기 전에는, 복잡하게만 느껴졌던 스프링 프레임워크가 Node.js나 파이썬와 같이 추상화된 개념이 많이들어간 타 프레임워크만큼은 아니지만, 비교적 쉽게 다가왔다.

여전히 스프링 프레임워크에 대한 이해가 필요하지만, 이번 프로젝트를 통해 가치있는 경험을 했다고 생각한다.


2. MQTT 프로토콜에 대한 이해.

MQTT프로토콜을 써야 했던 이유는 경량화된 프로토콜에 대한 수요에서 시작되었다. [wiki MQTT](https://ko.wikipedia.org/wiki/MQTT)

Mbed OS가 설치된 보드위에서 서버와 통신하기 프로토콜로 적절하다고 판단되었고, 실제로 테스트하면서도 간편함에 두번 놀랬다.

일단 간략한 사용방법은 다음 사이트에서 확인할 수 있다. 

<https://wnsgml972.github.io/mqtt/mqtt_ubuntu-install.html>  

더 자세한 내용은 아래 두 사이트를 참조하자.  

<https://wnsgml972.github.io/mqtt/mqtt.html>

<http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html>



이제 myCaps에서는 어떻게 쓰였는지 보면, 

MQTT에서 voice level을 받아 어느 정도 이상을 받으면 스프링부트는 mBED 에게 위험하다는 신호를 MQTT로 보낸다.



Maven 설정의 경우, 
```java
<dependency>
            <groupId>org.eclipse.paho</groupId>
            <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
            <version>1.2.0</version>
        </dependency>
```





먼저, MQTT에서 voice level을 받는 부분을 먼저 보면,

```java
private static void voiceLevelSubscribe() throws MqttException {
        System.out.println("== START VOICE LEVEL SUBSCRIBER ==");
        String url = Constant.ServerURI;
        String topic = Constant.VOICE_MQTT_TOPIC;

        MqttCallback callback = new voiceLevelCallback(new VoiceSender());
        setMqttConfig(url, topic, callback);
    }
```

```java
private static void setMqttConfig(String url, String topic, MqttCallback callback) throws MqttException {
        MqttClient client = new MqttClient(url, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        client.connect(options);
        client.setCallback(callback);
        client.subscribe(topic);
    }
```

```java
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class voiceLevelCallback implements MqttCallback {

    private VoiceSender alarmSender;

    public voiceLevelCallback(VoiceSender alarmSender) {
        this.alarmSender = alarmSender;
    }

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        alarmSender.add(new String(mqttMessage.getPayload()));
        System.out.println("Message received:\n\t" + new String(mqttMessage.getPayload()));
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}
```



최종적으로 `        client.subscribe(topic);` 에서 topic에 맞는 데이터를 받아오는 부분이다. 이 때 람다식을 활용해 `client.subscribe((a,b) -> {})`  이런 형태로 콜백에대한 응답도 설정할 수 있다.(여기서는 Callback 을 따로 만듬.)



이번에는 Data를 MQTT를 통해서 보내는 부분에 대한 코드를 살펴보면, 일단 전송을 하는 코드는

```java
MqttPublishClient2 client = new MqttPublishClient2();
client.send(topic, message)
```



```java
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublishClient2 {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MqttPublishClient.class);

    public void send(String topic, String content) {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient sampleClient = null;

        try {

            sampleClient = new MqttClient("tcp://" + "52.141.36.28" + ":" + "1883", RandomStringUtils.randomAlphanumeric(5), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            //http://www.hivemq.com/blog/mqtt-essentials-part-7-persistent-session-queuing-messages
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);
            logger.info("send(): Publishing message: " + content + " to topic: " + topic);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(Integer.parseInt("2"));
            message.setRetained(Boolean.FALSE);

            sampleClient.publish(topic, message);
            sampleClient.disconnect();
        } catch (MqttException e) {
            logger.info("Error on send with MqttClient...");
        }
    }
}

```



이런 식으로 비교적 단순하다고 표현할 수 있지만, 문제는 각각의 클라이언트를 어떻게 설정하냐에 따라 다르다.

특히, 새로운 객체를 생성해 만들 경우, 각각의 MQTT Client가 동일한 이름을 가져서는 안되고, Qos도 명확하게 설정해야 한다. 또한 데이터는 바이트어레이로 보내기 때문에, 받을 때도 받은 bytes array를 잘 변환시켜 활용해야 한다.



 이번 개발을 하면서 단순히 MQTT을 send하는건 문제가 없었지만- 멀티파트를 통해 데이터를 전송하고 특정 수치가 올라갔을 때 경고를 알려주는 코드에서 MQTT 콜백안에서 또다른 MQTT 프로토콜을 쓰면서 문제가 발생했다.



코드에서 AlarmSender.java를 보면 static queue를 만들어 새로운 스레드에서 queue에 있는 task를 처리하게 코드를 구현하긴 했으나, 더 나은 방법은 싱글톤으로 MqttClient를 만들었다면, 이런 수고를 덜었을 것이다.



3. 텔레그램 봇 활용하기.

텔레그램 봇 활용하는 방법에 대해서 명확하지 못했는데, 이번 프로젝트를 통해서 알 수 있었다.

일단 예제로 활용했던 프로젝트의 경우,  

<https://github.com/xabgesagtx/telegram-spring-boot-starter>

라이브러리는 

<https://github.com/xabgesagtx/telegram-spring-boot-starter-example>



BaseTelegramService.java

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseTelegramService {

    @Autowired
    protected ObjectMapper objectMapper;
}
```

여기서 objectMapper 의 역할은 message를 보내고 그 결과를 리턴받는 용도이다.



```java
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
```

sendMsg의 경우 테스트가 되어, 사진으로 받는 것도 작업하던 도중 해커톤이 종료되었다.



각설하고, 위에 코드를 간단히 설명하면, URL을 통해서 GET에 메세지를 담아 보내면 봇에 응답이 온다.



다음에는 스프링부트의 좀더 세밀한 부분을 이해하고, 이를 코틀린으로도 구현할 수 있었으면 좋겠다.

