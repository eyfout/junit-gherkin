package ht.eyfout.junit.jupiter.api;

import org.junit.jupiter.api.DynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

final class StdGherkinDynamicTest<G extends GivenState, W extends WhenScope, T extends ThenScope> implements GherkinDynamicTest<G, W, T> {

    private final StateScopeProvider<G, W, T> provider;

    StdGherkinDynamicTest(StateScopeProvider<G, W, T> provider) {
        this.provider = provider;
    }

    @Override
    public FollowOn given(String label, Consumer<G> given) {
        G givenState = provider.givenState();
        given.accept(givenState);
        givenState.setLabel(label);
        return new StdFollowOn(provider, givenState);
    }

    static class StdFollowOn<G extends GivenState, W extends WhenScope, T extends ThenScope> implements FollowOn<W, T> {
        private final StateScopeProvider<G, W, T> provider;
        private final G givenState;
        private final List<W> whenScopes = new ArrayList<>();


        StdFollowOn(StateScopeProvider<G, W, T> provider, G givenState) {
            this.provider = provider;
            this.givenState = givenState;
        }

        @Override
        public FollowOn when(String label, Consumer<W> when) {
            W whenScope = provider.whenScope((G) givenState.copyWith());
            when.accept(whenScope);
            whenScope.setLabel(label);
            whenScopes.add(whenScope);
            return this;
        }

        private String displayName(WhenScope whenScope, String thenLabel, Optional<String> addOn){
            StringBuilder sb = new StringBuilder();
            String delimiter = " | ";
            givenState.getLabel().ifPresent(it -> sb.append("Given: ").append(it).append(delimiter));
            whenScope.getLabel().ifPresent(it -> sb.append("When: ").append(it).append(delimiter));
            Optional.ofNullable(thenLabel).ifPresent(it -> sb.append("Then: ").append(it).append(delimiter));
            addOn.ifPresent(it -> sb.append(" => ").append(it));
            return sb.toString();
        }

        @Override
        public Stream<DynamicTest> then(String label, Consumer<T> then) {
            return whenScopes.stream()
                    .flatMap(whenScope -> {
                        return whenScope.scopeExecutor(givenState.copyWith()).map(executor ->{
                            T thenScope = provider.thenScope(executor);
                            return DynamicTest.dynamicTest(displayName(whenScope, label, executor.displayName()), () ->{
                                then.accept(thenScope);
                            });
                        });
                    });

        }
    }


}
