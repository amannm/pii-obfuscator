package systems.cauldron.utility.privacy.mapper;

import systems.cauldron.utility.privacy.exception.ObfuscatedKeyNotFoundException;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalKeyMapper<T> extends KeyMapper<T> {

    private final Map<T, Function<String, String>> mappers;

    public LocalKeyMapper(Map<Integer, T> layout, Map<T, Function<String, String>> mappers) {
        super(layout, mappers);
        this.mappers = mappers;
    }

    @Override
    public Map<T, Map<String, String>> generateKeymaps(Map<T, Set<String>> results) {
        return results.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                e -> {
                    Function<String, String> keyMapper = mappers.get(e.getKey());
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
