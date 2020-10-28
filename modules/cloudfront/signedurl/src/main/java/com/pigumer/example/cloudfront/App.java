package com.pigumer.example.cloudfront;

import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

public class App implements RequestStreamHandler {

    private final ObjectMapper mapper;
    private final String region;
    private final String fileName;
    private final String domainName;
    private final String keyPairId;
    private final String privateKeyFileName;
    private final AmazonCloudFront client;

    public App() {
        mapper = new ObjectMapper();
        region = System.getenv("AWS_REGION");
        domainName = System.getenv("DOMAIN_NAME");
        keyPairId = System.getenv("KEY_PAIR_ID");
        fileName = "testfile"; // FIXME
        privateKeyFileName = System.getenv("PRIVATE_KEY_FILE_NAME");
        client = AmazonCloudFrontClientBuilder.standard().withRegion(region).build();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(privateKeyFileName)) {
            PrivateKey key = new PrivateKeyParser().convert(is);
            String signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    "https://" + domainName + "/" + fileName,
                    keyPairId,
                    key,
                    new Date()
            );
            context.getLogger().log(signedUrl);
        } catch (InvalidKeySpecException e) {
            throw new IOException(e);
        }
    }
}
