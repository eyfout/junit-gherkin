package ht.eyfout.junit.jupiter.gherkin.http.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder;

import java.util.function.Consumer;

final public class GjCGRequestBuilder<H extends GjCGRequestBuilder.GjCGHeaderParam,
        P extends GjCGRequestBuilder.GjCGPathParam,
        Q extends GjCGRequestBuilder.GjCGQueryParam> extends HttpAPIRequestBuilder {
    public GjCGRequestBuilder(GjCGHttpAPI<H, P, Q> api, GivenState givenState) {
        super(api, givenState);
    }

    @SuppressWarnings("unchecked")
    public GjCGRequestBuilder<H, P, Q> header(Consumer<H> consumer) {
        consumer.accept((H) new GjCGHeaderParam(this));
        return this;
    }

    @SuppressWarnings("unchecked")
    public GjCGRequestBuilder<H, P, Q> pathParams(Consumer<P> consumer) {
        consumer.accept((P) new GjCGPathParam(this));
        return this;
    }

    @SuppressWarnings("unchecked")
    public GjCGRequestBuilder<H, P, Q> queryParams(Consumer<Q> consumer) {
        consumer.accept((Q) new GjCGQueryParam(this));
        return this;
    }

    public interface Param {
        void requiredParams(HttpAPIRequestBuilder builder);
    }

    final static public class GjCGHeaderParam implements Param {
        private final HttpAPIRequestBuilder builder;

        public GjCGHeaderParam(HttpAPIRequestBuilder builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpAPIRequestBuilder builder) {

        }
    }

    final static public class GjCGQueryParam implements Param {
        private final HttpAPIRequestBuilder builder;

        public GjCGQueryParam(HttpAPIRequestBuilder builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpAPIRequestBuilder builder) {
        }
    }

    final static public class GjCGPathParam implements Param {
        private final HttpAPIRequestBuilder builder;

        public GjCGPathParam(HttpAPIRequestBuilder builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpAPIRequestBuilder builder) {

        }
    }
}
