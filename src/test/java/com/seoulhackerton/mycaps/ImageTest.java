package com.seoulhackerton.mycaps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulhackerton.mycaps.domain.Image.ImageRes;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class ImageTest {
//     **********************************************
//     *** Update or verify the following values. ***
//     **********************************************
//
//     Replace <Subscription Key> with your valid subscription key.
    private static final String subscriptionKey = "f8081eb564f24b4080494119874019af";
//
//     You must use the same Azure region in your REST API method as you used to
//     get your subscription keys. For example, if you got your subscription keys
//     from the West US region, replace "westcentralus" in the URL
//     below with "westus".
//
//     Free trial subscription keys are generated in the "westus" region.
//     If you use a free trial subscription key, you shouldn't need to change
//     this region.
    private static final String uriBase =
            "https://koreacentral.api.cognitive.microsoft.com/vision/v2.0/analyze";

//        private static final String imageToAnalyze =
//            "http://ojsfile.ohmynews.com/STD_IMG_FILE/2015/0210/IE001800093_STD.jpg";
    private static final String imageToAnalyze =
            "/Users/lenkim/toy-project/mycaps/attachments/jumping-sample.jpeg";


    public static void main(String[] args) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder builder = new URIBuilder(uriBase);

            // Request parameters. All of them are optional.
            builder.setParameter("visualFeatures", "Categories,Description,Color");
            builder.setParameter("language", "en");

            // Prepare the URI for the REST API method.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            byte[] a = readBytesFromFile(imageToAnalyze);

            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
            // Request body.
            HttpEntity byteArrayEntity = new ByteArrayEntity(a);
            request.setEntity(byteArrayEntity);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
//                System.out.println(jsonString);
                ObjectMapper mapper = new ObjectMapper(); // create once, reuse
                ImageRes value = mapper.readValue(jsonString, ImageRes.class);
                System.out.println("REST Response:\n");
                System.out.println(value.toString());
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
    }

    private static byte[] readBytesFromFile(String filePath) throws FileNotFoundException {

        FileInputStream fileInputStream = null;

        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            System.out.println(file.getName());
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }
}
