package ht.eyfout.junit.jupiter.gherkin.http.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPI;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder;

import java.util.Optional;

public class GjCGHttpAPI<
        H extends GjCGRequestBuilder.GjCGHeader,
        P extends GjCGRequestBuilder.GjCGPath,
        Q extends GjCGRequestBuilder.GjCGQuery> implements HttpAPI<GjCGRequestBuilder<H, P, Q>> {
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
        return Optional.ofNullable(null);
    }

    @Override
    public GjCGRequestBuilder<H, P, Q> builder(GivenState givenState) {
        return new GjCGRequestBuilder<>( this, givenState);
    }
}
