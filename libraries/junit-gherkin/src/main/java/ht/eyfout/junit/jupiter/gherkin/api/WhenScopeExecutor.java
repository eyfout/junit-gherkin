package ht.eyfout.junit.jupiter.gherkin.api;

import java.util.Optional;

/**
 * Executes the request against the system under test.
 */
public interface WhenScopeExecutor {
    default Optional<String> getLabel() {
        return Optional.empty();
    }

    <R> R exec();
}
