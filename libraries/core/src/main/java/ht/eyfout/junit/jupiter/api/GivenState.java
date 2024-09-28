package ht.eyfout.junit.jupiter.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class GivenState implements MutableScope {
    private final Map<Object, Object> miscellaneous;
    public GivenState(){
        miscellaneous = new HashMap<>();
    }

    final Optional<String> getLabel() {
        return Optional.ofNullable((String) miscellaneous.get(GivenState.class));
    }

    final void setLabel(String label) {
        miscellaneous.put(GivenState.class, label);
    }

    public abstract GivenState copy();

    final GivenState copyWith() {
        GivenState other = copy();
        other.miscellaneous.putAll(miscellaneous);
        return other;
    }

    abstract public Map<String, Object> asMap();
}
