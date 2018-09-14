package systems.cauldron.utility;

import java.util.*;

public class RecordKeyScanner<T extends Enum<T>> {

    private final Map<T, Set<String>> keysets;
    private final Map<Integer, Set<String>> collectors;

    public RecordKeyScanner(Class<T> clazz) {
        keysets = new EnumMap<>(clazz);
        collectors = new HashMap<>();
    }

    public void setKeyColumn(int index, T type) {
        collectors.put(index, keysets.computeIfAbsent(type, t -> new HashSet<>()));
    }

    public void scan(String[] record) {
        collectors.forEach((index, keys) -> keys.add(record[index]));
    }

    public Map<T, Set<String>> getResults() {
        return Collections.unmodifiableMap(keysets);
    }

}
