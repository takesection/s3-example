package com.pigumer.example.cloudfront;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private int statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
