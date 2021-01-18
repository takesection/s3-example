package com.pigumer.example.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class App implements RequestStreamHandler {

    private final ObjectMapper mapper;
    private final String region;
    private final String bucketName;
    private final AmazonS3 client;

    public App() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        region = System.getenv("AWS_REGION");
        bucketName = System.getenv("BUCKET_NAME");
        client = AmazonS3ClientBuilder.standard().withRegion(region).build();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        String requestJson;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            int c;
            while ((c = inputStream.read()) != -1) {
               os.write(c);
            }
            requestJson = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }
        Request request = mapper.readValue(requestJson, Request.class);
        context.getLogger().log(requestJson);

        String path = request.getPathParameters().get("path");
        Map<String, String> queryStringParameters = request.getQueryStringParameters();
        String version = null;
        if (null != queryStringParameters) {
           version = queryStringParameters.get("versionId");
        }

        Date expiredTime = new Date(Instant.now().plusSeconds(60 * 60).toEpochMilli());

        Result result = new Result();

        GeneratePresignedUrlRequest putPresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, path).
                withMethod(HttpMethod.PUT).
                withExpiration(expiredTime);

        GeneratePresignedUrlRequest getPresignedUrlRequest;
        if (version == null) {
            getPresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, path).
                    withMethod(HttpMethod.GET).
                    withExpiration(expiredTime);
        } else {
            getPresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, path).
                    withMethod(HttpMethod.GET).
                    withExpiration(expiredTime).
                    withVersionId(version);
        }

        result.setPutUrl(client.generatePresignedUrl(putPresignedUrlRequest).toExternalForm());
        result.setGetUrl(client.generatePresignedUrl(getPresignedUrlRequest).toExternalForm());

        Response response = new Response();
        response.setStatusCode(200);
        response.getHeaders().put(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setBody(mapper.writeValueAsString(result));

        mapper.writeValue(outputStream, response);
    }
}
