package ht.eyfout.junit.jupiter.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class WhenScope implements ImmutableScope {
    Map<Object, Object> miscellaneous = new HashMap<>();

    final Optional<String> getLabel() {
        return Optional.ofNullable((String) miscellaneous.get(ThenScope.class));
    }

    final void setLabel(String label) {
        miscellaneous.put(ThenScope.class, label);
    }

    abstract public Stream<WhenScopeExecutor> scopeExecutor(GivenState given);
}
