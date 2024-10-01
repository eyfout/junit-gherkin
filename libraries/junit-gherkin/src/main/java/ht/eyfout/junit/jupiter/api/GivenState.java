package ht.eyfout.junit.jupiter.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GivenState implements MutableScope {
    private final Map<Object, Object> miscellaneous;

    public GivenState() {
        miscellaneous = new HashMap<>();
    }

    final Optional<String> getLabel() {
        return Optional.ofNullable((String) miscellaneous.get(GivenState.class));
    }

    final void setLabel(String label) {
        miscellaneous.put(GivenState.class, label);
    }

     GivenState copy(){
        return new GivenState();
     }

    @SuppressWarnings("unchecked")
    final <G extends GivenState> G copyWith() {
        GivenState other = copy();
        other.miscellaneous.putAll(miscellaneous);
        return (G) other;
    }

     public Map<String, Object> asMap(){
        return new HashMap<>();
     }
}
