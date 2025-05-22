package ht.eyfout.junit.jupiter.gherkin.api;

import org.junit.jupiter.api.DynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

final class StdGherkinDynamicTest<G extends GivenState, W extends WhenScope, T extends ThenScope> implements GherkinDynamicTest<G, W, T> {

    private final StateScopeProvider<G, W, T> provider;

    StdGherkinDynamicTest(StateScopeProvider<G, W, T> provider) {
        this.provider = provider;
    }

    @Override
    final public FollowOn<G, W, T> given(String label, Consumer<G> given) {
        G givenState = provider.givenState();
        given.accept(givenState);
        givenState.setLabel(label);
        return new StdFollowOn<>(provider, givenState);
    }

    static class StdFollowOn<G extends GivenState, W extends WhenScope, T extends ThenScope> implements FollowOn<G, W, T> {
        private final StateScopeProvider<G, W, T> provider;
        private final G givenState;
        private final List<W> whenScopes = new ArrayList<>();


        StdFollowOn(StateScopeProvider<G, W, T> provider, G givenState) {
            this.provider = provider;
            this.givenState = givenState;
        }

        @Override
        final public Stream<DynamicTest> fork(Function<FollowOn<G, W, T>, Stream<DynamicTest>>... fork) {
            return Arrays.stream(fork).flatMap(it ->
                    it.apply(new StdFollowOn<>(provider, this.givenState.copyWith()))
            );
        }

        @Override
        final public FollowOn<G, W, T> when(String label, Consumer<W> when) {
            W whenScope = provider.whenScope(givenState.copyWith());
            when.accept(whenScope);
            whenScope.setLabel(label);
            whenScopes.add(whenScope);
            return this;
        }

        @Override
        public FollowOn<G, W, T> and(String label, Consumer<G> given) {
            if(whenScopes.isEmpty()){
                given.accept(givenState);
                if(null != label) {
                    if(givenState.getLabel().isPresent()){
                        givenState.setLabel(givenState.getLabel().get() + " AND " + label);
                    } else {
                        givenState.setLabel(label);
                    }
                }
            } else {
                throw new IllegalStateException("and can only be applied immediately after a given.");
            }
            return this;
        }

        @Override
        final public Stream<DynamicTest> then(String label, Consumer<T> then) {
            return whenScopes.stream()
                    .flatMap(whenScope -> whenScope.scopeExecutor(givenState.copyWith()).map(executor -> {
                        T thenScope = provider.thenScope(executor);
                        DisplayNameGenerator displayName = provider.displayName();
                        givenState.getLabel().ifPresent(displayName::given);
                        whenScope.getLabel().ifPresent(displayName::when);
                        executor.getLabel().ifPresent(displayName::whenExecutor);
                        return DynamicTest.dynamicTest(
                                displayName.then(label).build(),
                                () -> then.accept(thenScope));
                    }));

        }
    }
}
