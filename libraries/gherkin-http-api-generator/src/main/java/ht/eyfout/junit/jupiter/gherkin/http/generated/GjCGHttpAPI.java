package ht.eyfout.junit.jupiter.gherkin.http.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPI;

import java.util.Optional;

public class GjCGHttpAPI<
        H extends GjCGRequestBuilder.GjCGHeaderParam,
        P extends GjCGRequestBuilder.GjCGPathParam,
        Q extends GjCGRequestBuilder.GjCGQueryParam> implements HttpAPI<GjCGRequestBuilder<H, P, Q>> {
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
    public GjCGRequestBuilder<H, P, Q> builder(GivenState givenState) {
        return new GjCGRequestBuilder<>(this, givenState);
    }
}
