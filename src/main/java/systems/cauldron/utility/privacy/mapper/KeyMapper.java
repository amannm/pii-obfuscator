package systems.cauldron.utility.privacy.mapper;

import systems.cauldron.utility.privacy.exception.KeyTypeMapperNotFoundException;

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

    static <T> void validate(Map<Integer, T> layout, Map<T, ?> mappers) {
        for (T type : layout.values()) {
            if (!mappers.containsKey(type)) {
                throw new KeyTypeMapperNotFoundException(type.toString());
            }
        }
    }
}
