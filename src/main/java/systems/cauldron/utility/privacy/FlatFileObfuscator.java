package systems.cauldron.utility.privacy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class FlatFileObfuscator<T extends Enum<T>> {

    private final KeyTransformer<T> transformer;

    public FlatFileObfuscator(KeyTransformer<T> transformer) {
        this.transformer = transformer;
    }

    public void obfuscate(String delimiter, Path source, Path destination) throws IOException {

        Map<Integer, T> columnKeyTypes = transformer.getColumnKeyTypes();

        RecordKeyScanner<T> scanner = new RecordKeyScanner<>(columnKeyTypes);

        Files.lines(source, StandardCharsets.UTF_8)
                .map(line -> line.split(delimiter, -1))
                .forEach(scanner::scan);

        Map<T, Map<String, String>> scannedKeymaps = transformer.generateScannedKeymaps(scanner);

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
