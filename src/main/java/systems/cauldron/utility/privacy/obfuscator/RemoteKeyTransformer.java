package systems.cauldron.utility.privacy.obfuscator;

import systems.cauldron.utility.privacy.exception.ObfuscatedKeyNotFoundException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RemoteKeyTransformer<T extends Enum<T>> extends KeyTransformer<T> {

    private RemoteKeyTransformer(Map<Integer, T> columnKeyTypeMap) {
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
                    if (!keyMapSet.containsAll(scannedKeys)) {
                        throw new ObfuscatedKeyNotFoundException();
                    }
                    keyMapSet.retainAll(scannedKeys);
                    return keyMap;
                }));
    }

    abstract Map<String, String> mapScannedKeys(T type, Set<String> scannedKeys);

}
