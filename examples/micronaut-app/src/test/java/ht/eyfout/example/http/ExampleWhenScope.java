package ht.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.http.HttpAPIWhenScope;

public class ExampleWhenScope extends HttpAPIWhenScope {

    public ExampleWhenScope(GivenState givenState) {
        super(givenState);
    }

    public String getAuthorization() {
        return ((ExampleGivenState) givenState).authorization;
    }

    public String getManufacturerID() {
        return ((ExampleGivenState) givenState).manufacturerID;
    }

    public String getVehicleID() {
        return ((ExampleGivenState) givenState).vehicleID;
    }

}
