package systems.cauldron.utility.privacy.obfuscator;

import java.util.*;

class RecordKeyScanner<T extends Enum<T>> {

    private final Map<T, Set<String>> keysets;
    private final ColumnKeyScanner[] scanners;

    RecordKeyScanner(Map<Integer, T> columnKeyTypes) {
        this.keysets = new HashMap<>();
        this.scanners = columnKeyTypes.entrySet().stream()
                .map(e -> new ColumnKeyScanner(e.getKey(), keysets.computeIfAbsent(e.getValue(), x -> new HashSet<>())))
                .toArray(ColumnKeyScanner[]::new);
    }

    void scan(String[] record) {
        for (ColumnKeyScanner scanner : scanners) {
            scanner.scan(record);
        }
    }

    Map<T, Set<String>> getResults() {
        return Collections.unmodifiableMap(keysets);
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

}
