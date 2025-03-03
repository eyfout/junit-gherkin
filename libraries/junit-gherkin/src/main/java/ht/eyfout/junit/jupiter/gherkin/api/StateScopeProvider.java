package ht.eyfout.junit.jupiter.gherkin.api;

/**
 * A Factory for creating the {@link Given} state and {@link When} {@link Then} scopes.
 * @param <Given> {@link GivenState}
 * @param <When> {@link WhenScope}
 * @param <Then> {@link ThenScope}
 */
public interface StateScopeProvider<Given extends GivenState, When extends WhenScope, Then extends ThenScope> {
    /**
     * Initial state, see {@link GivenState}.
     */
    Given givenState();

    /**
     * see {@link WhenScope}
     * @param givenState
     * @return
     */
    When whenScope(Given givenState);

    /**
     * see {@link ThenScope}
     * @param executor
     * @return
     */
    Then thenScope(WhenScopeExecutor executor);
}
