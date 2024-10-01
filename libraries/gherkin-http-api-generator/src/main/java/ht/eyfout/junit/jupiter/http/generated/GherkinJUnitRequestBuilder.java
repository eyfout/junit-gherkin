package ht.eyfout.junit.jupiter.http.generated;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.http.HttpAPI;
import ht.eyfout.junit.jupiter.api.http.HttpAPIRequestBuilder;

import java.util.function.Consumer;

public class GherkinJUnitRequestBuilder<H, P, Q> extends HttpAPIRequestBuilder {
    public GherkinJUnitRequestBuilder(HttpAPI<HttpAPIRequestBuilder> api, GivenState givenState) {
        super(api, givenState);
    }

    public GherkinJUnitRequestBuilder<H, P, Q> header(Consumer<H> consumer){
        consumer.accept((H)new GherkinJUnitHeaderParam(this));
        return this;
    }

    public GherkinJUnitRequestBuilder<H, P, Q> pathParams(Consumer<P> consumer){
        consumer.accept((P)new GherkinJUnitPathParam(this));
        return this;
    }
    public GherkinJUnitRequestBuilder<H, P, Q> queryParams(Consumer<Q> consumer){
        consumer.accept((Q)new GherkinJUnitQueryParam(this));
        return this;
    }

    static public class GherkinJUnitHeaderParam {
        private final HttpAPIRequestBuilder builder;
        public GherkinJUnitHeaderParam(HttpAPIRequestBuilder builder){
            this.builder = builder;
        }
    }

    static public class GherkinJUnitQueryParam {
        private final HttpAPIRequestBuilder builder;
        public GherkinJUnitQueryParam(HttpAPIRequestBuilder builder){
            this.builder = builder;
        }
    }

    static public class GherkinJUnitPathParam {
        private final HttpAPIRequestBuilder builder;
        public GherkinJUnitPathParam(HttpAPIRequestBuilder builder){
            this.builder = builder;
        }
    }
}
