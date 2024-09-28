package com.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.GivenState;

import java.util.Map;

public class ExampleGivenState extends GivenState{
    ExampleGivenState(){
    }

    @Override
    public ExampleGivenState copy() {
        ExampleGivenState other =  new ExampleGivenState();
        other.isolationID = isolationID;
        other.applicationID = applicationID;
        return other;
    }

    @Override
    public Map<String, Object> asMap() {
        return Map.of("isolationID", isolationID, "applicationID",applicationID);
    }

    public String isolationID = "iso";
    public String applicationID = "app";
}
