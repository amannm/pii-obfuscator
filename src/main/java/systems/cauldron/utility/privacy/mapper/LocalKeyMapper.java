package systems.cauldron.utility.privacy.mapper;

import systems.cauldron.utility.privacy.exception.KeyTypeMapperNotFoundException;
import systems.cauldron.utility.privacy.exception.ObfuscatedKeyNotFoundException;
import systems.cauldron.utility.privacy.operations.RecordKeyScanner;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalKeyMapper<T> extends KeyMapper<T> {

    private final Map<T, Function<String, String>> keyMappers;

    public LocalKeyMapper(Map<Integer, T> columnKeyTypeMap, Map<T, Function<String, String>> keyMappers) {
        super(columnKeyTypeMap);
        for (T type : columnKeyTypeMap.values()) {
            if (!keyMappers.containsKey(type)) {
                throw new KeyTypeMapperNotFoundException(type.toString());
            }
        }
        this.keyMappers = keyMappers;
    }

    @Override
    public Map<T, Map<String, String>> generateScannedKeymaps(RecordKeyScanner<T> scanner) {
        Map<T, Set<String>> results = scanner.getResults();
        return results.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                e -> {
                    Function<String, String> keyMapper = keyMappers.get(e.getKey());
                    return e.getValue().stream().collect(Collectors.toUnmodifiableMap(
                            k -> k,
                            k -> {
                                String obfuscatedKey = keyMapper.apply(k);
                                if (obfuscatedKey == null) {
                                    throw new ObfuscatedKeyNotFoundException(k);
                                }
                                return obfuscatedKey;
                            }));
                }));
    }

}