package systems.cauldron.utility;

import java.util.UUID;
import java.util.stream.Stream;

public class FileLocalUuidKeyMapper implements KeyMapper {

    @Override
    public Stream<KeyPair> apply(Stream<String> keys) {
        return keys.map(k -> new KeyPair(k, UUID.randomUUID().toString().replace("-", "")));
    }
}
