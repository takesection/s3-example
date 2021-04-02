package com.pigumer.example.cloudfront;

import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Date;

public class App implements RequestStreamHandler {

    private final ObjectMapper mapper;
    private final String region;

    private final String domainName;

    private final String keyPairId;
    private final String keyBucketName;
    private final String keyFile;

    private final AmazonCloudFront client;
    private final AmazonS3 s3Client;

    private final PrivateKey privateKey;

    public App() throws IOException {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        region = System.getenv("AWS_REGION");

        domainName = System.getenv("DOMAIN_NAME");

        keyPairId = System.getenv("KEY_PAIR_ID");
        keyBucketName = System.getenv("KEY_BUCKET_NAME");
        keyFile = System.getenv("KEY_FILE");

        client = AmazonCloudFrontClientBuilder.standard().withRegion(region).build();
        s3Client = AmazonS3ClientBuilder.standard().withForceGlobalBucketAccessEnabled(true).build();

        privateKey = loadPrivateKey();
    }

    PrivateKey loadPrivateKey() throws IOException {
        GetObjectRequest request = new GetObjectRequest(keyBucketName, keyFile);
        S3Object object = s3Client.getObject(request);
        try (InputStream is = object.getObjectContent()) {
           return new PrivateKeyParser().convert(is);
        } catch (InvalidKeySpecException e) {
            throw new IOException(e);
        }
    }

    String getSignedUrl(String content) {
        Date now = new Date();
        Date expiration = new Date(Instant.now().plusSeconds(60 * 60).toEpochMilli());

        String resourcePath = "https://" + domainName + "/" + content;
        /*
        String policy = CloudFrontUrlSigner.buildCustomPolicyForSignedUrl(
                null,
                expiration,
                "0.0.0.0/0",
                null);

        return CloudFrontUrlSigner.getSignedURLWithCustomPolicy(
                resourcePath,
                keyPairId,
                privateKey,
                policy
        );
        */
        return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                resourcePath,
                keyPairId,
                privateKey,
                expiration);
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
        context.getLogger().log(requestJson);
        Request request = mapper.readValue(requestJson, Request.class);

        String path = request.getPathParameters().get("path");
        String versionId = null;
        if (request.getQueryStringParameters() != null) {
            versionId = request.getQueryStringParameters().get("versionId");
        }
        if (versionId != null) {
            path = path + "?versionId=" + versionId;
        }

        String signedUrl = getSignedUrl(path);
        context.getLogger().log(signedUrl);

        Result result = new Result();
        result.setGetUrl(signedUrl);

        Response response = new Response();
        response.setStatusCode(200);
        response.setBody(mapper.writeValueAsString(result));

        mapper.writeValue(outputStream, response);
    }
}
