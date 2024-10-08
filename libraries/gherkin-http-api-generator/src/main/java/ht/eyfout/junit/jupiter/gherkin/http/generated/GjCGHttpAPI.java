package ht.eyfout.junit.jupiter.gherkin.http.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPI;

import java.util.Optional;

final public class GjCGHttpAPI implements HttpAPI<GjCGRequestBuilder<GjCGRequestBuilder.GjCGHeaderParam, GjCGRequestBuilder.GjCGPathParam, GjCGRequestBuilder.GjCGQueryParam>> {
    static final GjCGHttpAPI INSTANCE = new GjCGHttpAPI();

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
    public GjCGRequestBuilder<GjCGRequestBuilder.GjCGHeaderParam, GjCGRequestBuilder.GjCGPathParam, GjCGRequestBuilder.GjCGQueryParam> builder(GivenState givenState) {
        return new GjCGRequestBuilder<>(this, givenState);
    }
}
