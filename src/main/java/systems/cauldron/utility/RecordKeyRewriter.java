package systems.cauldron.utility;

import java.util.HashMap;
import java.util.Map;

public class RecordKeyRewriter {

    private final ColumnKeyRewriter[] rewriters;

    private RecordKeyRewriter(ColumnKeyRewriter[] rewriters) {
        this.rewriters = rewriters;
    }

    void rewrite(String[] record) {
        for (ColumnKeyRewriter rewriter : rewriters) {
            rewriter.rewrite(record);
        }
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    private static class ColumnKeyRewriter {

        private final int index;
        private final Map<String, String> keymap;

        ColumnKeyRewriter(int index, Map<String, String> keymap) {
            this.index = index;
            this.keymap = keymap;
        }

        void rewrite(String[] record) {
            record[index] = keymap.get(record[index]);
        }

    }

    public static class Builder {

        private final Map<Integer, Map<String, String>> keyColumnMaps;

        private Builder() {
            keyColumnMaps = new HashMap<>();
        }

        public Builder withKeyColumn(int index, Map<String, String> keymap) {
            keyColumnMaps.put(index, keymap);
            return this;
        }

        public RecordKeyRewriter build() {
            ColumnKeyRewriter[] rewriters = keyColumnMaps.entrySet().stream()
                    .map(e -> new ColumnKeyRewriter(e.getKey(), e.getValue()))
                    .toArray(ColumnKeyRewriter[]::new);
            return new RecordKeyRewriter(rewriters);
        }

    }

}
