package systems.cauldron.utility.privacy;

import systems.cauldron.utility.privacy.mapper.KeyMapper;
import systems.cauldron.utility.privacy.operations.RecordKeyRewriter;
import systems.cauldron.utility.privacy.operations.RecordKeyScanner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class FlatFileObfuscator<T> {

    private final KeyMapper<T> mapper;

    public FlatFileObfuscator(KeyMapper<T> mapper) {
        this.mapper = mapper;
    }

    public void obfuscate(String delimiter, Path source, Path destination) throws IOException {
        Map<Integer, T> layout = mapper.getLayout();
        Map<T, Set<String>> scannedKeys = scan(source, delimiter, layout);
        Map<T, Map<String, String>> scannedKeymaps = mapper.generateKeymaps(scannedKeys);
        RecordKeyRewriter<T> rewriter = new RecordKeyRewriter<>(layout, scannedKeymaps);
        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
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

    private static <T> Map<T, Set<String>> scan(Path source, String delimiter, Map<Integer, T> layout) throws IOException {
        RecordKeyScanner<T> scanner = new RecordKeyScanner<>(layout);
        Files.lines(source, StandardCharsets.UTF_8)
                .map(line -> line.split(delimiter, -1))
                .forEach(scanner::scan);
        return scanner.getResults();
    }


}
