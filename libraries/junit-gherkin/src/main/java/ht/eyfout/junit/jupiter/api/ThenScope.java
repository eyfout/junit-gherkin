package ht.eyfout.junit.jupiter.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ThenScope implements ImmutableScope, WhenScopeExecutor {
    protected final WhenScopeExecutor executor;
    Map<Object, Object> miscellaneous = new HashMap<>();

    public ThenScope(WhenScopeExecutor executor) {
        this.executor = executor;
    }

    final Optional<String> getLabel() {
        return Optional.ofNullable((String) miscellaneous.get(ThenScope.class));
    }

    final void setLabel(String label) {
        miscellaneous.put(ThenScope.class, label);
    }

    @Override
    public <R> R exec() {
        return executor.exec();
    }
}
