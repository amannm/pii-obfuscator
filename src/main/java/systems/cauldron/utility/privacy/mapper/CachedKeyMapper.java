package systems.cauldron.utility.privacy.mapper;

import systems.cauldron.utility.privacy.exception.ObfuscatedKeyNotFoundException;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CachedKeyMapper<T> extends KeyMapper<T> {

    private final Map<T, Function<Set<String>, Map<String, String>>> mappers;

    public CachedKeyMapper(Map<Integer, T> layout, Map<T, Function<Set<String>, Map<String, String>>> keysetMappers) {
        super(layout);
        validate(layout, keysetMappers);
        this.mappers = keysetMappers;
    }

    @Override
    public Map<T, Map<String, String>> generateKeymaps(Map<T, Set<String>> results) {
        return results.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    Set<String> scannedKeys = e.getValue();
                    Map<String, String> keyMap = mappers.get(e.getKey()).apply(scannedKeys);
                    Set<String> keyMapSet = keyMap.keySet();
                    keyMapSet.retainAll(scannedKeys);
                    if (keyMapSet.size() != scannedKeys.size()) {
                        throw new ObfuscatedKeyNotFoundException();
                    }
                    return keyMap;
                }));
    }

}
