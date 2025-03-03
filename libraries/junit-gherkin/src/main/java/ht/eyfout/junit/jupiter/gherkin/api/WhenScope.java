package ht.eyfout.junit.jupiter.gherkin.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Describe the action or event the user performed
 */
public abstract class WhenScope implements ImmutableScope {
    Map<Object, Object> miscellaneous = new HashMap<>();

    final Optional<String> getLabel() {
        return Optional.ofNullable((String) miscellaneous.get(WhenScope.class));
    }

    final void setLabel(String label) {
        miscellaneous.put(WhenScope.class, label);
    }

    abstract public Stream<WhenScopeExecutor> scopeExecutor(GivenState given);
}
