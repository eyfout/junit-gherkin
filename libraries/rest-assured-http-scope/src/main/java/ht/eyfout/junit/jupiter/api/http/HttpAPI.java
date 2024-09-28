package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;

public abstract class HttpAPI<B> {
    abstract public String getHttpMethod();

    abstract public String getBasePath();

    abstract public String getDescription();

    public <B extends HttpAPIRequestExecutor> B executor(HttpAPIRequestBuilder builder, GivenState givenState) {
        return (B) new HttpAPIRequestExecutor(this, builder, givenState);
    }

    @Override
    public String toString() {
        return getHttpMethod().toUpperCase() + " " + getBasePath();
    }
}
