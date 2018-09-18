package systems.cauldron.utility.privacy.mapper;

import systems.cauldron.utility.privacy.operations.RecordKeyScanner;

import java.util.Map;

public abstract class KeyMapper<T> {

    private final Map<Integer, T> layout;

    KeyMapper(Map<Integer, T> layout) {
        this.layout = layout;
    }

    public Map<Integer, T> getLayout() {
        return layout;
    }

    public abstract Map<T, Map<String, String>> generateKeymaps(RecordKeyScanner<T> scanner);

}
