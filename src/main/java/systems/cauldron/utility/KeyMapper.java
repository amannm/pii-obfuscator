package systems.cauldron.utility;

import java.util.function.Function;
import java.util.stream.Stream;

public interface KeyMapper extends Function<Stream<String>, Stream<KeyPair>> {

    Stream<KeyPair> apply(Stream<String> keys);

}
