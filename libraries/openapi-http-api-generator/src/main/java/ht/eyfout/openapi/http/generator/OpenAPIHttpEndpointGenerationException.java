package ht.eyfout.openapi.http.generator;

class OpenAPIHttpEndpointGenerationException extends RuntimeException {
    public OpenAPIHttpEndpointGenerationException(String msg, Throwable t) {
        super(msg, t);
    }
}
