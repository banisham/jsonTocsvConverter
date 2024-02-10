package com.sc.hcv.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;

public class RestApiUtils {


    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T invokeApiWithJWT(String endpoint, String jwtToken, Class<T> responseType) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(endpoint);
        httpGet.addHeader("Authorization", "Bearer " + jwtToken);

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String jsonResponse = EntityUtils.toString(entity);
            return parseJson(jsonResponse, responseType);
        } else {
            return null;
        }
    }

    private static HttpClient createHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();
            return HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating HttpClient", e);
        }
    }

    private static <T> T parseJson(String jsonResponse, Class<T> responseType) {
        try {
            return objectMapper.readValue(jsonResponse, responseType);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }

}

