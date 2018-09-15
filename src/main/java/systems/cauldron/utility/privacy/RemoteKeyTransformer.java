package systems.cauldron.utility.privacy;

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
                e -> mapScannedKeys(e.getKey(), e.getValue())));
    }

    abstract Map<String, String> mapScannedKeys(T type, Set<String> scannedKeys);

}
