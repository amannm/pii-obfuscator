package systems.cauldron.utility.privacy.mapper;

import systems.cauldron.utility.privacy.operations.RecordKeyScanner;

import java.util.Map;

public abstract class KeyMapper<T> {

    private final Map<Integer, T> columnKeyTypeMap;

    KeyMapper(Map<Integer, T> columnKeyTypeMap) {
        this.columnKeyTypeMap = columnKeyTypeMap;
    }

    public Map<Integer, T> getColumnKeyTypes() {
        return columnKeyTypeMap;
    }

    public abstract Map<T, Map<String, String>> generateScannedKeymaps(RecordKeyScanner<T> scanner);

}
