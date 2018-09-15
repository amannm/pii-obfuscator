package systems.cauldron.utility.privacy;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RemoteKeyTransformer<T extends Enum<T>> extends KeyTransformer<T> {

    private final Map<T, Function<Set<String>, Map<String, String>>> keyMappers;

    public RemoteKeyTransformer(Map<Integer, T> columnKeyTypeMap, Map<T, Function<Set<String>, Map<String, String>>> keyMappers) {
        super(columnKeyTypeMap);
        this.keyMappers = keyMappers;
    }

    @Override
    public Map<T, Map<String, String>> generateScannedKeymaps(RecordKeyScanner<T> scanner) {
        return scanner.getResults().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
            Function<Set<String>, Map<String, String>> setMapFunction = keyMappers.get(e.getKey());
            return setMapFunction.apply(e.getValue());
        }));
    }

}
