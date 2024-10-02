package ht.eyfout.junit.jupiter.gherkin.http.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPI;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder;

import java.util.function.Consumer;

public class GjCGRequestBuilder<    H extends GjCGRequestBuilder.GjCGHeader,
        P extends GjCGRequestBuilder.GjCGPath,
        Q extends GjCGRequestBuilder.GjCGQuery> extends HttpAPIRequestBuilder {
    public GjCGRequestBuilder(GjCGHttpAPI<H,P,Q> api, GivenState givenState) {
        super(api, givenState);
    }

    public GjCGRequestBuilder<H, P, Q> header(Consumer<H> consumer) {
        consumer.accept((H) new GjCGHeader(this));
        return this;
    }

    public GjCGRequestBuilder<H, P, Q> pathParams(Consumer<P> consumer) {
        consumer.accept((P) new GjCGPath(this));
        return this;
    }

    public GjCGRequestBuilder<H, P, Q> queryParams(Consumer<Q> consumer) {
        consumer.accept((Q) new GjCGQuery(this));
        return this;
    }

    static public class GjCGHeader {
        private final HttpAPIRequestBuilder builder;

        public GjCGHeader(HttpAPIRequestBuilder builder) {
            this.builder = builder;
        }
    }

    static public class GjCGQuery {
        private final HttpAPIRequestBuilder builder;

        public GjCGQuery(HttpAPIRequestBuilder builder) {
            this.builder = builder;
        }
    }

    static public class GjCGPath {
        private final HttpAPIRequestBuilder builder;

        public GjCGPath(HttpAPIRequestBuilder builder) {
            this.builder = builder;
        }
    }
}
