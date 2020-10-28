package com.pigumer.example.cloudfront;

import com.amazonaws.auth.PEM;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public class PrivateKeyParser {

    public PrivateKey convert(InputStream is) throws IOException, InvalidKeySpecException {
        return PEM.readPrivateKey(is);
    }
}
