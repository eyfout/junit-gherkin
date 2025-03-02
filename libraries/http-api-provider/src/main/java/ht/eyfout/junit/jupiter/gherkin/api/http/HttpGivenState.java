package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.http.HttpEndpoint;
import ht.eyfout.http.HttpRequestBuilder;
import ht.eyfout.junit.jupiter.gherkin.api.GivenState;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class HttpGivenState extends GivenState {
    /**
     * Matches the endpoint with the client that will execute the call to the
     * provider of the endpoint using the information captured in the request.
     * @param endpoint provider endpoint
     * @param request request parameters
     * @param response expected response from provider
     */
    protected abstract <B extends HttpRequestBuilder, R> void match(HttpEndpoint<B> endpoint,
                                  B request,
                                  Supplier<R> response);

    /**
     * HTTP Request
     * @param endpoint
     * @param consumer
     * @return
     * @param <B>
     */
    final public <B extends HttpRequestBuilder> Response httpRequest(HttpEndpoint<B> endpoint, Consumer<B> consumer){
        B builder = endpoint.builder();
        consumer.accept(builder);
        return new Response() {
            @Override
            public <R> void respondsWith(Supplier<R> httpResponse) {
                match(endpoint, builder, httpResponse);
            }
        };
    }

    public interface Response{
        <R> void respondsWith(Supplier<R> httpResponse);
    }

    /**
     * {@link GivenState} represented as a Map.
     * @return
     */
    public Map<String, Object> asMap(){
        return Map.of();
    }
}
