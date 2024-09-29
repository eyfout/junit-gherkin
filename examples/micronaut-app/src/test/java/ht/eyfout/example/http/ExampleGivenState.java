package ht.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.GivenState;

import java.util.Map;

public class ExampleGivenState extends GivenState {
    public String authorization = "iso";
    public String manufacturerID = "app";
    public String vehicleID = "";

    ExampleGivenState() {
    }

    @Override
    public ExampleGivenState copy() {
        ExampleGivenState other = new ExampleGivenState();
        other.authorization = authorization;
        other.manufacturerID = manufacturerID;
        other.vehicleID = vehicleID;
        return other;
    }

    @Override
    public Map<String, Object> asMap() {
        return Map.of("isolationID", authorization, "applicationID", manufacturerID);
    }
}
