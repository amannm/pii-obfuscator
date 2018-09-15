package systems.cauldron.utility;

import java.util.Map;
import java.util.stream.Collectors;

public class RecordKeyRewriter<T> {

    private final ColumnKeyRewriter[] rewriters;

    public RecordKeyRewriter(Map<Integer, T> columnKeyTypes, Map<T, Map<String, String>> typeKeymaps) {
        this.rewriters = columnKeyTypes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> typeKeymaps.get(e.getValue())))
                .entrySet().stream()
                .map(e -> new ColumnKeyRewriter(e.getKey(), e.getValue()))
                .toArray(ColumnKeyRewriter[]::new);
    }

    void rewrite(String[] record) {
        for (ColumnKeyRewriter rewriter : rewriters) {
            rewriter.rewrite(record);
        }
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

}
