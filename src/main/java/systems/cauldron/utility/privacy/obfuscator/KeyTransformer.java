package systems.cauldron.utility.privacy.obfuscator;

import java.util.Map;

public abstract class KeyTransformer<T extends Enum<T>> {

    private final Map<Integer, T> columnKeyTypeMap;

    KeyTransformer(Map<Integer, T> columnKeyTypeMap) {
        this.columnKeyTypeMap = columnKeyTypeMap;
    }

    Map<Integer, T> getColumnKeyTypes() {
        return columnKeyTypeMap;
    }

    public abstract Map<T, Map<String, String>> generateScannedKeymaps(RecordKeyScanner<T> scanner);

}
