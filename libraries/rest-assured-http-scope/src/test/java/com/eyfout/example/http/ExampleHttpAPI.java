package com.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.http.HttpAPI;

public class ExampleHttpAPI extends HttpAPI {
    public ExampleHttpAPI(){

    }
    @Override
    public String getHttpMethod() {
        return "GET";
    }

    @Override
    public String getBasePath() {
        return "v1/isolations/{isolationID}/applications/{applicationID}/rules";
    }

    @Override
    public String getDescription() {
        return "query";
    }
}
