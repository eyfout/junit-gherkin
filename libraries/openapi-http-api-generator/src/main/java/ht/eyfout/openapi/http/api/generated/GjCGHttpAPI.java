package ht.eyfout.openapi.http.api.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPI;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder;

import javax.annotation.processing.Generated;
import java.util.Optional;
import java.util.function.Consumer;

@Generated("junit-gherkin")
final public class GjCGHttpAPI implements HttpAPI<GjCGHttpAPI.RequestBuilder<GjCGHttpAPI.HeaderParam, GjCGHttpAPI.PathParam, GjCGHttpAPI.QueryParam>> {
    public static final GjCGHttpAPI INSTANCE = new GjCGHttpAPI();

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
    public RequestBuilder<HeaderParam, PathParam, QueryParam> builder(GivenState givenState) {
        return new RequestBuilder<>(this, givenState);
    }

    public interface Param {
        void requiredParams(HttpAPIRequestBuilder builder);
    }

    @Generated("junit-gherkin")
    static final public class RequestBuilder<H extends HeaderParam,
            P extends PathParam,
            Q extends QueryParam> extends HttpAPIRequestBuilder {
        public RequestBuilder(GjCGHttpAPI api, GivenState givenState) {
            super(api, givenState);
        }

        @SuppressWarnings("unchecked")
        public RequestBuilder<H, P, Q> header(Consumer<H> consumer) {
            consumer.accept((H) new HeaderParam(this));
            return this;
        }

        @SuppressWarnings("unchecked")
        public RequestBuilder<H, P, Q> pathParams(Consumer<P> consumer) {
            consumer.accept((P) new PathParam(this));
            return this;
        }

        @SuppressWarnings("unchecked")
        public RequestBuilder<H, P, Q> queryParams(Consumer<Q> consumer) {
            consumer.accept((Q) new QueryParam(this));
            return this;
        }

    }

    @Generated("junit-gherkin")
    final static public class HeaderParam implements Param {
        private final HttpAPIRequestBuilder builder;

        public HeaderParam(RequestBuilder<?, ?, ?> builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpAPIRequestBuilder builder) {

        }
    }

    @Generated("junit-gherkin")
    final static public class QueryParam implements Param {
        private final HttpAPIRequestBuilder builder;

        public QueryParam(RequestBuilder<?, ?, ?> builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpAPIRequestBuilder builder) {
        }
    }

    @Generated("junit-gherkin")
    final static public class PathParam implements Param {
        private final HttpAPIRequestBuilder builder;

        public PathParam(RequestBuilder<?, ?, ?> builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpAPIRequestBuilder builder) {

        }
    }
}
