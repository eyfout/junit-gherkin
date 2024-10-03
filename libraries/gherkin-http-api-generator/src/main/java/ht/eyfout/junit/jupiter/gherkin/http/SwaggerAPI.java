package ht.eyfout.junit.jupiter.gherkin.http;


import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.Optional;

record SwaggerAPI(String path, PathItem.HttpMethod method, Operation operation) {
    public String httpMethod() {
        return method.name().toUpperCase();
    }

    public String id() {
        String operationID = operation.getOperationId();
        if (operationID.toLowerCase().startsWith(method.name().toLowerCase())) {
            operationID = operationID.substring(method.name().length());
        }
        return httpMethod() + operationID;
    }

    public String description() {
        return Optional.ofNullable(operation.getSummary()).orElseGet(() ->
                Optional.ofNullable(operation.getDescription()).orElse(null)
        );
    }


}