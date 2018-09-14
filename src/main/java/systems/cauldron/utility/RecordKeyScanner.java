package systems.cauldron.utility;

import java.util.*;

public class RecordKeyScanner<T extends Enum<T>> {

    private final ColumnKeyScanner[] scanners;
    private final Map<T, Set<String>> keysets;

    private RecordKeyScanner(ColumnKeyScanner[] scanners, Map<T, Set<String>> keysets) {
        this.scanners = scanners;
        this.keysets = keysets;
    }

    void scan(String[] record) {
        for (ColumnKeyScanner scanner : scanners) {
            scanner.scan(record);
        }
    }

    public Set<String> getResults(T type) {
        return Collections.unmodifiableSet(keysets.get(type));
    }

    public static <U extends Enum<U>> Builder<U> createBuilder(Class<U> clazz) {
        return new Builder<>(clazz);
    }

    private static class ColumnKeyScanner {

        private final int index;
        private final Set<String> keys;

        private ColumnKeyScanner(int index, Set<String> keys) {
            this.index = index;
            this.keys = keys;
        }

        void scan(String[] record) {
            keys.add(record[index]);
        }

    }

    public static class Builder<T extends Enum<T>> {

        private final Map<T, Set<String>> keysets;
        private final Map<Integer, Set<String>> keyColumnScanners;

        private Builder(Class<T> clazz) {
            keysets = new EnumMap<>(clazz);
            keyColumnScanners = new HashMap<>();
        }

        public Builder<T> withKeyColumn(int index, T type) {
            keyColumnScanners.put(index, keysets.computeIfAbsent(type, t -> new HashSet<>()));
            return this;
        }

        public RecordKeyScanner<T> build() {
            ColumnKeyScanner[] scanners = keyColumnScanners.entrySet().stream()
                    .map(e -> new ColumnKeyScanner(e.getKey(), e.getValue()))
                    .toArray(ColumnKeyScanner[]::new);
            return new RecordKeyScanner<>(scanners, keysets);
        }
    }

}
