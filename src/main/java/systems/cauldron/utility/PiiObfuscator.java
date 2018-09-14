package systems.cauldron.utility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PiiObfuscator {

    private final Path path;
    private final String delimiter;

    public PiiObfuscator(Path path, String delimiter) {
        this.path = path;
        this.delimiter = delimiter;
    }

    public void scan(RecordKeyScanner scanner) throws IOException {
        Files.lines(path, StandardCharsets.UTF_8)
                .map(line -> line.split(delimiter, -1))
                .forEach(scanner::scan);
    }

    public void rewrite(RecordKeyRewriter rewriter) throws IOException {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(null, null);
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                Files.lines(path, StandardCharsets.UTF_8)
                        .map(line -> line.split(delimiter, -1))
                        .peek(rewriter::rewrite)
                        .map(fields -> String.join(delimiter, fields))
                        .forEach(str -> {
                            try {
                                writer.write(str);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        } finally {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile);
                } catch (IOException ignored) {
                }
            }
        }

    }
}
