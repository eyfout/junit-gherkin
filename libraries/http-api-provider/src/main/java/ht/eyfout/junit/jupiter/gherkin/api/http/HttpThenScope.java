package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.junit.jupiter.gherkin.api.ThenScope;
import ht.eyfout.junit.jupiter.gherkin.api.WhenScopeExecutor;
import io.restassured.response.Response;

public class HttpThenScope extends ThenScope {
    HttpThenScope(WhenScopeExecutor executor) {
        super(executor);
    }

    final public Response httpResponse() {
        return exec();
    }
}
