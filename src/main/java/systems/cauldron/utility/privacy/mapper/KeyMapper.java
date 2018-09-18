package systems.cauldron.utility.privacy.mapper;

import java.util.Map;
import java.util.Set;

public abstract class KeyMapper<T> {

    private final Map<Integer, T> layout;

    KeyMapper(Map<Integer, T> layout) {
        this.layout = layout;
    }

    public Map<Integer, T> getLayout() {
        return layout;
    }

    public abstract Map<T, Map<String, String>> generateKeymaps(Map<T, Set<String>> scanner);

}
