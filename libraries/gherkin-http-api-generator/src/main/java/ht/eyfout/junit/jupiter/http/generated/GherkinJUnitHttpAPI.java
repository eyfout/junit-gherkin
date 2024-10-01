package ht.eyfout.junit.jupiter.http.generated;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.http.HttpAPI;

import java.util.Optional;

public class GherkinJUnitHttpAPI<
        H extends GherkinJUnitRequestBuilder.GherkinJUnitHeaderParam,
        P extends GherkinJUnitRequestBuilder.GherkinJUnitPathParam,
        Q extends GherkinJUnitRequestBuilder.GherkinJUnitQueryParam> implements HttpAPI<GherkinJUnitRequestBuilder<H, P, Q>> {
    @Override
    public String getHttpMethod() {
        return "";
    }

    @Override
    public String getBasePath() {
        return "";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.empty();
    }

    @Override
    public GherkinJUnitRequestBuilder<H, P, Q> builder(GivenState givenState) {
        return HttpAPI.super.builder(givenState);
    }
}
