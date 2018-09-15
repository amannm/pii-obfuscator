package systems.cauldron.utility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileObfuscator<T extends Enum<T>> {

    private final Map<Integer, T> columnKeyTypes;
    private final RecordKeyScanner<T> scanner;
    private final Map<T, Function<String, String>> keyMappers;

    public FileObfuscator(Map<Integer, T> columnKeyTypes, Map<T, Function<String, String>> keyMappers) {
        this.columnKeyTypes = columnKeyTypes;
        this.scanner = new RecordKeyScanner<>(columnKeyTypes);
        this.keyMappers = keyMappers;
    }

    public void obfuscate(String delimiter, Path source, Path destination) throws IOException {

        Files.lines(source, StandardCharsets.UTF_8)
                .map(line -> line.split(delimiter, -1))
                .forEach(scanner::scan);

        Map<T, Set<String>> scannedKeysets = scanner.getResults();

        Map<T, Map<String, String>> scannedKeymaps = scannedKeysets.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                e -> {
                    Function<String, String> keyMapper = keyMappers.get(e.getKey());
                    return e.getValue().stream().collect(Collectors.toMap(
                            k -> k,
                            k -> {
                                String obfuscatedKey = keyMapper.apply(k);
                                if (obfuscatedKey == null) {
                                    throw new UnmappedKeyException(k);
                                }
                                return obfuscatedKey;
                            }));
                }));

        RecordKeyRewriter<T> rewriter = new RecordKeyRewriter<>(columnKeyTypes, scannedKeymaps);

        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            Files.lines(source, StandardCharsets.UTF_8)
                    .map(line -> line.split(delimiter, -1))
                    .peek(rewriter::rewrite)
                    .map(fields -> String.join(delimiter, fields))
                    .forEach(str -> {
                        try {
                            writer.write(str);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

}
