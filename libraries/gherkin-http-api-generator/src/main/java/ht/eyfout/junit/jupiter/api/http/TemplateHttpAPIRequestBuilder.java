package ht.eyfout.junit.jupiter.api.http;

import java.util.function.Consumer;

public class TemplateHttpAPIRequestBuilder<H, P, Q> extends HttpAPIRequestBuilder {

    TemplateHttpAPIRequestBuilder<H,P,Q> header(Consumer<H> header){
        return this;
    }
    TemplateHttpAPIRequestBuilder<H,P,Q> path(Consumer<P> path){
        return this;
    }
    TemplateHttpAPIRequestBuilder<H,P,Q> query(Consumer<Q> query){
        return this;
    }

}
