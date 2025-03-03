package ht.eyfout.junit.jupiter.gherkin.api;

import org.junit.jupiter.api.DynamicTest;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface GherkinDynamicTest<Given extends GivenState, When extends WhenScope, Then extends ThenScope> {
    public static <Given extends GivenState, When extends WhenScope, Then extends ThenScope> GherkinDynamicTest<Given, When, Then> dynamicTest(
            StateScopeProvider<Given, When, Then> provider) {
        return new StdGherkinDynamicTest<>(provider);
    }

    /**
     * @see GivenState
     * @param label optional label
     * @return {@link FollowOn} for chaining
     */
    FollowOn<Given, When, Then> given(String label, Consumer<Given> given);

    /**
     * @see WhenScope
     * @param label optional label
     * @return {@link FollowOn} for chaining
     */
    default FollowOn<Given, When, Then> when(String label, Consumer<When> when){
        return given(null, it -> {}).when(label, when);
    }

    /**
     * Assert your expecting without a state or criteria.
     * @param label
     * @param then
     * @return
     */
    default Stream<DynamicTest> expect(String label, Consumer<Then> then){
        return when(null, it -> {}).then(label, then);
    }

    /**
     * For chaining of {@link GherkinDynamicTest} to specify {@link When} and {@link Then} criterion.
     * @param <When> {@link FollowOn#when(String, Consumer)}
     * @param <Then> {@link FollowOn#then(String, Consumer)}
     */
    interface FollowOn<Given extends GivenState, When extends WhenScope, Then extends ThenScope> {

        /**
         * Bifurcate the given state into multiple {@link FollowOn} that MUST
         * terminate with {@link #then(String, Consumer)}.
         *
         * @param fork
         * @return {@link }
         */
        Stream<DynamicTest> fork(Function<FollowOn<Given, When, Then>, Stream<DynamicTest>>... fork);

        /**
         * @see WhenScope
         * @param label optional label
         * @return {@link FollowOn} for chaining
         */
        FollowOn<Given, When, Then> when(String label, Consumer<When> when);


        /**
         * Continue {@link GivenState}.
         * @param label
         * @param when
         * @return
         */
        FollowOn<Given, When, Then> and(String label, Consumer<Given> when);

        /**
         * @see ThenScope
         * @param label
         * @return {@link Stream} of {@link DynamicTest}.
         */
        Stream<DynamicTest> then(String label, Consumer<Then> then);
    }
}
