package systems.cauldron.utility;

import java.util.HashMap;
import java.util.Map;

public class RecordKeyRewriter {

    private final Map<Integer, Map<String, String>> replacers;

    public RecordKeyRewriter() {
        replacers = new HashMap<>();
    }

    public void setColumnMap(int index, Map<String, String> keymap) {
        replacers.put(index, keymap);
    }

    public void rewrite(String[] record) {
        replacers.forEach((index, keymap) -> record[index] = keymap.get(record[index]));
    }

}
