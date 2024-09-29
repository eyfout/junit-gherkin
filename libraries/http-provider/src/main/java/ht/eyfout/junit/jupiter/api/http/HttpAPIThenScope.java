package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.ThenScope;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;
import io.restassured.response.Response;

public class HttpAPIThenScope extends ThenScope {
    HttpAPIThenScope(WhenScopeExecutor executor) {
        super(executor);
    }

    final public Response httpResponse() {
        return exec();
    }
}
