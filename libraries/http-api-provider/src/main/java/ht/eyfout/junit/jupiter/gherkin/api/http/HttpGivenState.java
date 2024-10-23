package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.http.HttpEndpoint;
import ht.eyfout.http.HttpRequestBuilder;
import ht.eyfout.junit.jupiter.gherkin.api.GivenState;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class HttpGivenState extends GivenState {
    protected abstract void match(HttpEndpoint<?> endpoint, HttpRequestBuilder request, Supplier<Object> response);

    public <B extends HttpRequestBuilder> Response httpRequest(HttpEndpoint<B> endpoint, Consumer<B> consumer){
        B builder = endpoint.builder();
        consumer.accept(builder);
        return new Response() {
            @Override
            public void respondsWith(Supplier<Object> httpResponse) {
                match(endpoint, builder, httpResponse);
            }
        };
    }

    public interface Response{
        void respondsWith(Supplier<Object> httpResponse);
    }
}
