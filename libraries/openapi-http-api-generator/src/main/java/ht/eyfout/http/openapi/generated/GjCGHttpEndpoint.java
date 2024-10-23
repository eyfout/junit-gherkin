package ht.eyfout.http.openapi.generated;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.http.HttpEndpoint;
import ht.eyfout.http.HttpRequestBuilder;

import javax.annotation.processing.Generated;
import java.util.Optional;
import java.util.function.Consumer;

@Generated("junit-gherkin")
final public class GjCGHttpEndpoint implements HttpEndpoint<GjCGHttpEndpoint.RequestBuilder<GjCGHttpEndpoint.HeaderParam, GjCGHttpEndpoint.PathParam, GjCGHttpEndpoint.QueryParam>> {
    public static final GjCGHttpEndpoint INSTANCE = new GjCGHttpEndpoint();

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
        void requiredParams(HttpRequestBuilder builder);
    }

    @Generated("junit-gherkin")
    static final public class RequestBuilder<H extends HeaderParam,
            P extends PathParam,
            Q extends QueryParam> extends HttpRequestBuilder {
        public RequestBuilder(GjCGHttpEndpoint api, GivenState givenState) {
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
        private final HttpRequestBuilder builder;

        public HeaderParam(RequestBuilder<?, ?, ?> builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpRequestBuilder builder) {

        }
    }

    @Generated("junit-gherkin")
    final static public class QueryParam implements Param {
        private final HttpRequestBuilder builder;

        public QueryParam(RequestBuilder<?, ?, ?> builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpRequestBuilder builder) {
        }
    }

    @Generated("junit-gherkin")
    final static public class PathParam implements Param {
        private final HttpRequestBuilder builder;

        public PathParam(RequestBuilder<?, ?, ?> builder) {
            this.builder = builder;
            this.builder.requireThat(this::requiredParams);
        }

        @Override
        public void requiredParams(HttpRequestBuilder builder) {

        }
    }
}
