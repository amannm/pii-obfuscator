package systems.cauldron.utility.privacy;

import java.util.Map;

public abstract class KeyTransformer<T extends Enum<T>> {

    private final Map<Integer, T> columnKeyTypeMap;

    public KeyTransformer(Map<Integer, T> columnKeyTypeMap) {
        this.columnKeyTypeMap = columnKeyTypeMap;
    }

    public Map<Integer, T> getColumnKeyTypes() {
        return columnKeyTypeMap;
    }

    public abstract Map<T, Map<String, String>> generateScannedKeymaps(RecordKeyScanner<T> scanner);

}
