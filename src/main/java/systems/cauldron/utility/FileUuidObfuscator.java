package systems.cauldron.utility;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUuidObfuscator<T extends Enum<T>> extends FileObfuscator<T> {

    public FileUuidObfuscator(Class<T> clazz, Map<Integer, T> columnKeyTypes) {
        super(clazz, columnKeyTypes);
    }

    @Override
    public Map<String, String> getKeyMap(Set<String> keys) {
        return keys.stream().collect(
                Collectors.toMap(
                        k -> k,
                        k -> UUID.randomUUID().toString().replace("-", "")
                )
        );
    }
}
