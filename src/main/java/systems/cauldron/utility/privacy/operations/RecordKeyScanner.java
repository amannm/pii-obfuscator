package systems.cauldron.utility.privacy.operations;

import java.util.*;

public class RecordKeyScanner<T> {

    private final Map<T, Set<String>> keysets;
    private final ColumnKeyScanner[] scanners;

    public RecordKeyScanner(Map<Integer, T> layout) {
        this.keysets = new HashMap<>();
        this.scanners = layout.entrySet().stream()
                .map(e -> new ColumnKeyScanner(e.getKey(), keysets.computeIfAbsent(e.getValue(), x -> new HashSet<>())))
                .toArray(ColumnKeyScanner[]::new);
    }

    public void scan(String[] record) {
        for (ColumnKeyScanner scanner : scanners) {
            scanner.scan(record);
        }
    }

    public Map<T, Set<String>> getResults() {
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
