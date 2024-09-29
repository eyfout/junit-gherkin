package ht.eyfout.junit.jupiter.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ThenScope implements ImmutableScope, WhenScopeExecutor {
    protected final WhenScopeExecutor executor;
    public ThenScope(WhenScopeExecutor executor) {
        this.executor = executor;
    }
    @Override
    public <R> R exec() {
        return executor.exec();
    }
}
