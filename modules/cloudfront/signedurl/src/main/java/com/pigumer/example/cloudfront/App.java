package com.pigumer.example.cloudfront;

import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class App implements RequestStreamHandler {

    private final ObjectMapper mapper;
    private final String region;
    private final String fileName;
    private final String bucketName;
    private final AmazonCloudFront client;

    public App() {
        mapper = new ObjectMapper();
        region = System.getenv("AWS_REGION");
        bucketName = System.getenv("BUCKET_NAME");
        fileName = "testfile"; // FIXME
        client = AmazonCloudFrontClientBuilder.standard().withRegion(region).build();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

    }
}
