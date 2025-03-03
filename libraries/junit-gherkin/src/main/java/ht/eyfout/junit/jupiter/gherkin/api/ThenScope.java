package ht.eyfout.junit.jupiter.gherkin.api;

/**
 * Expected output.
 */
public abstract class ThenScope implements ImmutableScope, WhenScopeExecutor {
    protected final WhenScopeExecutor executor;

    public ThenScope(WhenScopeExecutor executor) {
        this.executor = executor;
    }

    /**
     * Execute this {@link ThenScope}
     * @return
     * @param <R>
     */
    @Override
    final public <R> R exec() {
        return executor.exec();
    }
}
