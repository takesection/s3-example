package com.pigumer.example.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class App implements RequestStreamHandler {

    private final ObjectMapper mapper;
    private final String region;
    private final String fileName;
    private final String bucketName;
    private final AmazonS3 client;

    public App() {
        mapper = new ObjectMapper();
        region = System.getenv("AWS_REGION");
        bucketName = System.getenv("BUCKET_NAME");
        fileName = "testfile"; // FIXME
        client = AmazonS3ClientBuilder.standard().withRegion(region).build();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Result result = new Result();
        result.setPutUrl(client.generatePresignedUrl(bucketName, fileName, null, HttpMethod.PUT).toExternalForm());
        result.setGetUrl(client.generatePresignedUrl(bucketName, fileName, null, HttpMethod.GET).toExternalForm());
        mapper.writeValue(outputStream, result);
    }
}
