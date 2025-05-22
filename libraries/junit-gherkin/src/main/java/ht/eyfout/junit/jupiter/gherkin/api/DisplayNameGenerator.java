package ht.eyfout.junit.jupiter.gherkin.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Display name generator for the DynamicTest, when
 * specified for the {@link GivenState}, {@link  WhenScope} and or {@link ThenScope}.
 * Optionally {@link WhenScopeExecutor} may include a label to distinguish
 * the best based on the subject under test.
 */
public class DisplayNameGenerator {
    public static String SUFFIX = "suffix";

    private Map<Object, String> labels = new HashMap<>();
    private String delimiter = "|";

    public DisplayNameGenerator(){
    }

    /**
     * {@link GivenState} label
     * @param label
     * @return
     */
    public DisplayNameGenerator given(String label){
        labels.put(GivenState.class, label);
        return this;
    }

    /**
     * {@link WhenScope} label
     * @param label
     * @return
     */
    public DisplayNameGenerator when(String label){
        labels.put(WhenScope.class, label);
        return this;
    }

    /**
     * {@link ThenScope} label
     * @param label
     * @return
     */
    public DisplayNameGenerator then(String label){
        labels.put(ThenScope.class, label);
        return this;
    }

    /**
     * {@link WhenScopeExecutor} label
     * @param label
     * @return
     */
    public DisplayNameGenerator whenExecutor(String label){
        labels.put(SUFFIX, label);
        return this;
    }

    /**
     * Display name for {@link org.junit.jupiter.api.DynamicTest}.
     * @return
     */
    public String build(){
        StringBuilder sb = new StringBuilder();

        ifPresent(GivenState.class, it -> {
            sb.append(String.format(" Given: %s %s", it, delimiter ));
        });

        ifPresent(WhenScope.class, it -> {
            sb.append(String.format(" When: %s %s", it, delimiter ));
        });

        ifPresent(ThenScope.class, it -> {
            sb.append(String.format(" Then: %s %s", it, delimiter ));
        });

        ifPresent(SUFFIX, it -> {
            sb.append(String.format(" %s %s", delimiter, it ));
        });

        String result = sb.toString();
        return result.substring(0, result.length() - 1).trim();
    }

    private void ifPresent(Object key, Consumer<String> consumer){
        Optional.ofNullable(labels.get(key)).ifPresent(consumer);
    }
}
