package com.conquestreforged.connect.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

public final class Requests {

    private Requests() {
    }

    private static final CloseableHttpClient client;
    private static final Gson gson = new Gson();

    static {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();

        client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
    }

    public static Request<byte[]> getBytes(String address) {
        return () -> GET_BYTES.apply(address);
    }

    public static Request<JsonElement> getJson(String address) {
        return () -> GET_JSON.apply(address);
    }

    public static <T> Request.Function<JsonElement, T> unmarshal(Class<T> type) {
        return e -> gson.fromJson(e, type);
    }

    public static final Request.Function<String, byte[]> GET_BYTES = address -> {
        HttpGet get = new HttpGet(address);
        try (CloseableHttpResponse response = client.execute(get)) {
            try (InputStream in = response.getEntity().getContent()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtils.copy(in, out);
                out.flush();
                return out.toByteArray();
            }
        } finally {
            get.releaseConnection();
        }
    };

    public static final Request.Function<String, JsonElement> GET_JSON = address -> {
        HttpGet get = new HttpGet(address);
        try (CloseableHttpResponse response = client.execute(get)) {
            try (InputStream in = response.getEntity().getContent()) {
                Reader reader = new InputStreamReader(in);
                return new JsonParser().parse(new BufferedReader(reader));
            }
        } finally {
            get.releaseConnection();
        }
    };
}
