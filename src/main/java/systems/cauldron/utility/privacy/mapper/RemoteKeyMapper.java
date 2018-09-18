package systems.cauldron.utility.privacy.mapper;

import systems.cauldron.utility.privacy.exception.ObfuscatedKeyNotFoundException;
import systems.cauldron.utility.privacy.operations.RecordKeyScanner;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RemoteKeyMapper<T> extends KeyMapper<T> {

    private RemoteKeyMapper(Map<Integer, T> columnKeyTypeMap) {
        super(columnKeyTypeMap);
    }

    @Override
    public Map<T, Map<String, String>> generateScannedKeymaps(RecordKeyScanner<T> scanner) {
        return scanner.getResults().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    Set<String> scannedKeys = e.getValue();
                    Map<String, String> keyMap = mapScannedKeys(e.getKey(), scannedKeys);
                    Set<String> keyMapSet = keyMap.keySet();
                    keyMapSet.retainAll(scannedKeys);
                    if (keyMapSet.size() != scannedKeys.size()) {
                        throw new ObfuscatedKeyNotFoundException();
                    }
                    return keyMap;
                }));
    }

    abstract Map<String, String> mapScannedKeys(T type, Set<String> scannedKeys);

}
