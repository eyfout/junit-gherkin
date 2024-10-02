package ht.eyfout.junit.jupiter.gherkin.http.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder;

import java.util.function.Consumer;

public class GjCGRequestBuilder<    H extends GjCGRequestBuilder.GjCGHeaderParam,
        P extends GjCGRequestBuilder.GjCGPathParam,
        Q extends GjCGRequestBuilder.GjCGQueryParam> extends HttpAPIRequestBuilder {
    public GjCGRequestBuilder(GjCGHttpAPI<H,P,Q> api, GivenState givenState) {
        super(api, givenState);
    }

    public GjCGRequestBuilder<H, P, Q> header(Consumer<H> consumer) {
        consumer.accept((H) new GjCGHeaderParam(this));
        return this;
    }

    public GjCGRequestBuilder<H, P, Q> pathParams(Consumer<P> consumer) {
        consumer.accept((P) new GjCGPathParam(this));
        return this;
    }

    public GjCGRequestBuilder<H, P, Q> queryParams(Consumer<Q> consumer) {
        consumer.accept((Q) new GjCGQueryParam(this));
        return this;
    }

    static public class GjCGHeaderParam {
        private final HttpAPIRequestBuilder builder;

        public GjCGHeaderParam(HttpAPIRequestBuilder builder) {
            this.builder = builder;
        }
    }

    static public class GjCGQueryParam {
        private final HttpAPIRequestBuilder builder;

        public GjCGQueryParam(HttpAPIRequestBuilder builder) {
            this.builder = builder;
        }
    }

    static public class GjCGPathParam {
        private final HttpAPIRequestBuilder builder;

        public GjCGPathParam(HttpAPIRequestBuilder builder) {
            this.builder = builder;
        }
    }
}
