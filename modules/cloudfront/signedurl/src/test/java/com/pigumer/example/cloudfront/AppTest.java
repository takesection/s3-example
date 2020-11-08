package com.pigumer.example.cloudfront;

import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void testPrivateKeyParser() throws Exception {
        App fixture = new App();
        String signedUrl = fixture.getSignedUrl("test");
        System.out.println(signedUrl);
    }
}
