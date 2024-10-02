package ht.eyfout.junit.jupiter.gherkin.api;

public interface StateScopeProvider<Given extends GivenState, When extends WhenScope, Then extends ThenScope> {
    Given givenState();

    When whenScope(Given givenState);

    Then thenScope(WhenScopeExecutor executor);
}
