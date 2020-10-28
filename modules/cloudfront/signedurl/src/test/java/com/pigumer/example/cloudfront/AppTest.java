package com.pigumer.example.cloudfront;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Date;

public class AppTest {

    @Test
    public void testPrivateKeyParser() throws Exception {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("pk-APKAICNYWAA3PJ24GU6Q.pem")) {
            PrivateKey privateKey = new PrivateKeyParser().convert(is);
            String signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    "https://example.com/test",
                    "APKAICNYWAA3PJ24GU6Q",
                    privateKey,
                    new Date()
            );
            System.out.println(signedUrl);
        }
    }
}
