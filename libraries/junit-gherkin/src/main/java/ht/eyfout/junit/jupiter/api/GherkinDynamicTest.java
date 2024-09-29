package ht.eyfout.junit.jupiter.api;

import org.junit.jupiter.api.DynamicTest;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface GherkinDynamicTest<Given extends GivenState, When extends WhenScope, Then extends ThenScope> {
    static <Given extends GivenState, When extends WhenScope, Then extends ThenScope> GherkinDynamicTest<Given, When, Then> dynamicTest(
            StateScopeProvider<Given, When, Then> provider) {
        return new StdGherkinDynamicTest(provider);
    }

    FollowOn<When, Then> given(String label, Consumer<Given> given);

    interface FollowOn<When extends WhenScope, Then extends ThenScope> {

        Stream<DynamicTest> fork(Function<FollowOn<When, Then>, Stream<DynamicTest>>... fork);

        FollowOn<When, Then> when(String label, Consumer<When> when);

        Stream<DynamicTest> then(String label, Consumer<Then> then);
    }
}
