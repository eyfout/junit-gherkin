package ht.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.http.HttpAPI;
import ht.eyfout.junit.jupiter.api.http.HttpAPIRequestExecutor;

public class ExampleHttpAPI extends HttpAPI<HttpAPIRequestExecutor> {
    public static HttpAPI<HttpAPIRequestExecutor> INSTANCE = new ExampleHttpAPI();

    public ExampleHttpAPI() {

    }

    @Override
    public String getHttpMethod() {
        return "GET";
    }

    @Override
    public String getBasePath() {
        return "v1/vehicles";
    }

    @Override
    public String getDescription() {
        return "query";
    }
}
