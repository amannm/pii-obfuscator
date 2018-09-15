package systems.cauldron.utility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileObfuscator<T extends Enum<T>> {

    private final Map<Integer, T> columnKeyTypes;
    private final RecordKeyScanner<T> scanner;
    private final Function<String, String> mapper;

    public FileObfuscator(Class<T> clazz, Map<Integer, T> columnKeyTypes, Function<String, String> mapper) {
        this.columnKeyTypes = columnKeyTypes;
        RecordKeyScanner.Builder<T> scannerBuilder = RecordKeyScanner.createBuilder(clazz);
        columnKeyTypes.forEach(scannerBuilder::setKeyColumn);
        this.scanner = scannerBuilder.build();
        this.mapper = mapper;
    }

    public void obfuscate(String delimiter, Path source, Path destination) throws IOException {

        Files.lines(source, StandardCharsets.UTF_8)
                .map(line -> line.split(delimiter, -1))
                .forEach(scanner::scan);

        Map<T, Map<String, String>> scannerKeymaps = scanner.getKeysets().entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                e -> e.getValue().stream().collect(Collectors.toMap(
                        k -> k,
                        k -> {
                            String obfuscatedKey = mapper.apply(k);
                            if (obfuscatedKey == null) {
                                throw new UnmappedKeyException(k);
                            }
                            return obfuscatedKey;
                        }))));

        RecordKeyRewriter.Builder rewriterBuilder = RecordKeyRewriter.createBuilder();
        columnKeyTypes.forEach((i, t) -> rewriterBuilder.setKeyColumn(i, scannerKeymaps.get(t)));
        RecordKeyRewriter rewriter = rewriterBuilder.build();

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
